package com.wkc.library.service.agent;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardBookItem;
import com.wkc.library.entity.Advice;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookLike;
import com.wkc.library.entity.BookScore;
import com.wkc.library.entity.User;
import com.wkc.library.service.AdviceService;
import com.wkc.library.service.BookLikeService;
import com.wkc.library.service.BookScoreService;
import com.wkc.library.service.BookService;
import com.wkc.library.service.FeatureDashboardService;
import com.wkc.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PolarisAgentRouterService {

    private static final Pattern BOOK_NAME_IN_BRACKETS = Pattern.compile("《([^》]+)》");
    private static final Pattern BOOK_ID_PATTERN = Pattern.compile("(?:bookId|书籍ID|书id|图书ID|图书id)\\s*[:=：]?\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SCORE_PATTERN = Pattern.compile("(?:评分|打分|评)\\s*[:=：]?\\s*([1-5])(?:分)?");
    private static final Pattern LIKE_LEVEL_PATTERN = Pattern.compile("(?:喜好|偏好|like)\\s*[:=：]?\\s*([0-2])", Pattern.CASE_INSENSITIVE);
    private static final Pattern NATURAL_SCORE_PATTERN = Pattern.compile("给?\\s*([\\p{IsHan}A-Za-z0-9\\-·\\s]{1,40})\\s*(?:评|评分|打分)\\s*([1-5])\\s*分?");
    private static final Pattern SCORE_VALUE_PATTERN = Pattern.compile("([1-5])\\s*分?");

    private static final int FEATURE_BATCH_SIZE = 3;

    private static final List<FeatureBookRoute> FEATURE_BOOK_ROUTES = Arrays.asList(
            new FeatureBookRoute("Three-Body Problem", "三体", "/book/ThreeBody"),
            new FeatureBookRoute("Sapiens: A Brief History of Humankind", "人类简史", "/book/Sapiens"),
            new FeatureBookRoute("The Hitchhiker's Guide to the Galaxy", "银河系漫游指南", "/book/HitchhikersGuide"),
            new FeatureBookRoute("Interaction of Color", "色彩互动学", "/book/InteractionOfColor"),
            new FeatureBookRoute("The Non-Designer's Design Book", "写给大家看的设计书", "/book/NonDesignersDesignBook"),
            new FeatureBookRoute("Grid Systems in Graphic Design", "平面设计中的网格系统", "/book/GridSystems"),
            new FeatureBookRoute("Dune", "沙丘", "/book/Dune"),
            new FeatureBookRoute("1984", "1984", "/book/NineteenEightyFour"),
            new FeatureBookRoute("Brave New World", "美丽新世界", "/book/BraveNewWorld"),
            new FeatureBookRoute("Zen and the Art of Motorcycle Maintenance", "禅与摩托车维修艺术", "/book/Zen"),
            new FeatureBookRoute("Amusing Ourselves to Death", "娱乐至死", "/book/AmusingOurselvesToDeath"),
            new FeatureBookRoute("Life 3.0", "生命3.0", "/book/Life30"),
            new FeatureBookRoute("Journey to the West", "西游记", "/book/JourneyToTheWest"),
            new FeatureBookRoute("Three Kingdoms", "三国演义", "/book/ThreeKingdoms")
    );

    @Autowired
    private BookService bookService;
    @Autowired
    private BookLikeService bookLikeService;
    @Autowired
    private BookScoreService bookScoreService;
    @Autowired
    private AdviceService adviceService;
    @Autowired
    private FeatureDashboardService featureDashboardService;
    @Autowired
    private UserService userService;
    @Autowired
    private AgentMetricService agentMetricService;

    private final Map<Long, Integer> featuredOffsetByMemory = new ConcurrentHashMap<Long, Integer>();

    public PolarisAgentRouteResult routeAndHandle(String message, Long memoryId, Integer userId, String userName) {
        String text = message == null ? "" : message.trim();
        if (!StringUtils.hasText(text)) {
            return PolarisAgentRouteResult.notHandled();
        }

        if (isBookSuggestionIntent(text)) {
            return executeMissingBookAdviceAgent(userName, userId, memoryId, text, extractBookId(text), extractBookName(text), extractAdviceText(text));
        }
        if (isFeedbackAdviceIntent(text)) {
            return executeFeedbackAdviceAgent(userName, userId, memoryId, text, extractAdviceText(text));
        }
        if (isUserDataQueryIntent(text)) {
            return executeUserDataQueryAgent(userId, memoryId, text);
        }
        if (isPreferenceManageIntent(text)) {
            return executePreferenceManageAgent(userName, userId, memoryId, text, extractBookId(text), extractBookName(text), extractLikeLevel(text));
        }
        if (isScoreManageIntent(text)) {
            return executeScoreManageAgent(userName, userId, memoryId, text, extractBookId(text), extractBookName(text), extractScore(text));
        }
        if (isSystemSearchIntent(text)) {
            return executeSystemBookSearchAgent(userId, memoryId, text, extractSearchKeyword(text));
        }
        if (isFeaturedRecommendIntent(text, memoryId)) {
            boolean nextBatch = text.contains("换一批") || text.contains("再来一批");
            return executeFeaturedRecommendAgent(userId, memoryId, text, nextBatch);
        }
        return PolarisAgentRouteResult.notHandled();
    }

    public PolarisAgentRouteResult executeFeaturedRecommendAgent(Integer userId, Long memoryId, String requestText, boolean nextBatch) {
        long start = System.currentTimeMillis();
        String agentName = "featured_recommend_agent";
        try {
            LocalDate to = LocalDate.now();
            LocalDate from = to.minusDays(89);
            List<FeatureDashboardBookItem> rows = featureDashboardService.getBooks(
                    from.format(DateTimeFormatter.ISO_DATE),
                    to.format(DateTimeFormatter.ISO_DATE),
                    "acceptRate",
                    100
            );
            if (rows == null || rows.isEmpty()) {
                return handled(agentName, userId, memoryId, requestText, start, "特色推荐暂无统计数据，请稍后再试。");
            }

            List<FeatureDashboardBookItem> ranked = rows.stream()
                    .filter(item -> StringUtils.hasText(findFeaturePath(item.getBookName())))
                    .sorted(Comparator.comparingDouble(this::featureScore).reversed())
                    .collect(Collectors.toList());

            if (ranked.isEmpty()) {
                return handled(agentName, userId, memoryId, requestText, start, "当前暂无可展示的特色推荐图书，请稍后再试。");
            }

            int offset = 0;
            if (nextBatch && memoryId != null) {
                offset = featuredOffsetByMemory.getOrDefault(memoryId, 0) + FEATURE_BATCH_SIZE;
            }
            if (offset >= ranked.size()) offset = 0;
            if (memoryId != null) featuredOffsetByMemory.put(memoryId, offset);

            int end = Math.min(offset + FEATURE_BATCH_SIZE, ranked.size());
            StringBuilder sb = new StringBuilder("这些书有特色页面，已按“特色页分析高表现”优先排序：\n");
            for (int i = offset; i < end; i++) {
                FeatureDashboardBookItem item = ranked.get(i);
                String path = findFeaturePath(item.getBookName());
                String link = "[" + safe(item.getBookName()) + "](" + path + ")";
                sb.append(i - offset + 1).append(". ").append(link)
                        .append(" | 完成率 ").append(percent(item.getCompleteRate()))
                        .append(" | 接受率 ").append(percent(item.getAcceptRate())).append('\n');
            }
            sb.append("\n可继续说“换一批”获取下一组。\n\n- [进入阅读决策](/readingDecision)");
            return handled(agentName, userId, memoryId, requestText, start, sb.toString().trim());
        } catch (Exception e) {
            return notHandled(agentName, userId, memoryId, requestText, start, e);
        }
    }

    public PolarisAgentRouteResult executeSystemBookSearchAgent(Integer userId, Long memoryId, String requestText, String keyword) {
        long start = System.currentTimeMillis();
        String agentName = "system_catalog_search_agent";
        try {
            if (!StringUtils.hasText(keyword)) {
                return handled(agentName, userId, memoryId, requestText, start, "请给出检索关键词，例如：检索系统图书库《三体》");
            }
            List<Book> books = bookService.searchBookByLike(keyword);
            if (books == null || books.isEmpty()) {
                return handled(agentName, userId, memoryId, requestText, start, "系统图书库未检索到相关图书。");
            }
            StringBuilder sb = new StringBuilder("系统图书库检索结果：\n");
            int limit = Math.min(8, books.size());
            for (int i = 0; i < limit; i++) {
                Book b = books.get(i);
                String path = findFeaturePath(b.getBookName());
                String jump = StringUtils.hasText(path) ? " | [特色页面](" + path + ")" : "";
                sb.append(i + 1).append(". ").append(safe(b.getBookName()))
                        .append(" | ").append(safe(b.getBookAuthor()))
                        .append(" | ").append(safe(b.getBookClassify()))
                        .append(jump).append('\n');
            }
            return handled(agentName, userId, memoryId, requestText, start, sb.toString().trim());
        } catch (Exception e) {
            return notHandled(agentName, userId, memoryId, requestText, start, e);
        }
    }

    public PolarisAgentRouteResult executeUserDataQueryAgent(Integer userId, Long memoryId, String requestText) {
        long start = System.currentTimeMillis();
        String agentName = "user_data_query_agent";
        try {
            if (userId == null) {
                return handled(agentName, null, memoryId, requestText, start, "请先登录后再查询你的数据。");
            }
            List<BookLike> likes = bookLikeService.list(new LambdaQueryWrapper<BookLike>().eq(BookLike::getUserId, userId));
            List<BookScore> scores = bookScoreService.list(new LambdaQueryWrapper<BookScore>().eq(BookScore::getUserId, userId));

            String text = requestText == null ? "" : requestText;
            boolean queryLike = text.contains("喜好") || text.contains("书库");
            boolean queryScore = text.contains("评分");
            if (!queryLike && !queryScore) {
                queryLike = true;
                queryScore = true;
            }

            int limit = 10;
            StringBuilder sb = new StringBuilder("你当前有 ")
                    .append(likes.size()).append(" 条喜好记录，")
                    .append(scores.size()).append(" 条评分记录。");

            if (queryLike) {
                sb.append("\n\n【喜好记录】");
                if (likes.isEmpty()) {
                    sb.append("\n暂无喜好记录。");
                } else {
                    List<BookLike> likeRows = likes.stream().limit(limit).collect(Collectors.toList());
                    for (int i = 0; i < likeRows.size(); i++) {
                        BookLike row = likeRows.get(i);
                        sb.append("\n").append(i + 1).append(". ")
                                .append(safe(row.getBookName()))
                                .append(" | ").append(likeLabel(row.getLikeLevel()));
                    }
                    if (likes.size() > limit) sb.append("\n... 仅展示前 ").append(limit).append(" 条");
                }
            }

            if (queryScore) {
                sb.append("\n\n【评分记录】");
                if (scores.isEmpty()) {
                    sb.append("\n暂无评分记录。");
                } else {
                    List<BookScore> scoreRows = scores.stream().limit(limit).collect(Collectors.toList());
                    for (int i = 0; i < scoreRows.size(); i++) {
                        BookScore row = scoreRows.get(i);
                        Book book = row.getBookId() == null ? null : bookService.getById(row.getBookId());
                        String bookName = book == null ? "未知图书" : safe(book.getBookName());
                        sb.append("\n").append(i + 1).append(". ")
                                .append(bookName)
                                .append(" | ").append(row.getScore()).append(" 分");
                    }
                    if (scores.size() > limit) sb.append("\n... 仅展示前 ").append(limit).append(" 条");
                }
            }
            return handled(agentName, userId, memoryId, requestText, start, sb.toString());
        } catch (Exception e) {
            return notHandled(agentName, userId, memoryId, requestText, start, e);
        }
    }

    public PolarisAgentRouteResult executePreferenceManageAgent(String userName, Integer userId, Long memoryId, String requestText, Integer bookId, String bookName, Integer likeLevel) {
        long start = System.currentTimeMillis();
        String agentName = "preference_manage_agent";
        try {
            if (userId == null) return handled(agentName, null, memoryId, requestText, start, "请先登录后再调整喜好。");
            if (likeLevel == null) return handled(agentName, userId, memoryId, requestText, start, "请说明喜好：不想看/还行/想看。");
            Book book = resolveBook(bookId, bookName);
            if (book == null) return handled(agentName, userId, memoryId, requestText, start, "未匹配到目标书籍，请提供书名。");

            BookLike payload = new BookLike();
            payload.setUserId(userId);
            payload.setUserName(resolveUserName(userId, userName));
            payload.setBookId(book.getBookId());
            payload.setBookName(book.getBookName());
            payload.setLikeLevel(likeLevel);
            boolean ok = Boolean.TRUE.equals(bookLikeService.likeBook(payload));
            return handled(agentName, userId, memoryId, requestText, start, ok ? "喜好调整成功：" + book.getBookName() : "喜好调整失败。");
        } catch (Exception e) {
            return notHandled(agentName, userId, memoryId, requestText, start, e);
        }
    }

    public PolarisAgentRouteResult executeScoreManageAgent(String userName, Integer userId, Long memoryId, String requestText, Integer bookId, String bookName, Integer score) {
        long start = System.currentTimeMillis();
        String agentName = "score_manage_agent";
        try {
            if (userId == null) return handled(agentName, null, memoryId, requestText, start, "请先登录后再提交评分。");
            if (score == null || score < 1 || score > 5) return handled(agentName, userId, memoryId, requestText, start, "请提供1-5分。");
            Book book = resolveBook(bookId, bookName);
            if (book == null) return handled(agentName, userId, memoryId, requestText, start, "未匹配到目标书籍，请提供书名。");

            BookScore payload = new BookScore();
            payload.setUserId(userId);
            payload.setBookId(book.getBookId());
            payload.setScore(score);
            boolean ok = bookScoreService.updateScore(payload);
            return handled(agentName, userId, memoryId, requestText, start, ok ? "评分成功：" + book.getBookName() + " -> " + score : "评分失败。");
        } catch (Exception e) {
            return notHandled(agentName, userId, memoryId, requestText, start, e);
        }
    }

    // 保留方法名兼容历史调用：现在语义是“图书建议”
    public PolarisAgentRouteResult executeMissingBookAdviceAgent(String userName, Integer userId, Long memoryId, String requestText, Integer bookId, String bookName, String adviceText) {
        long start = System.currentTimeMillis();
        String agentName = "book_suggestion_agent";
        try {
            if (!StringUtils.hasText(adviceText)) return handled(agentName, userId, memoryId, requestText, start, "请补充图书建议内容。");

            Book resolvedBook = resolveBook(bookId, bookName);
            String finalBookId = null;
            String displayTarget = null;
            if (resolvedBook != null) {
                finalBookId = String.valueOf(resolvedBook.getBookId());
                displayTarget = resolvedBook.getBookName();
            } else if (bookId != null) {
                finalBookId = String.valueOf(bookId);
                displayTarget = "目标图书";
            } else if (StringUtils.hasText(bookName)) {
                finalBookId = "MISSING:" + bookName.trim();
                displayTarget = bookName.trim() + "（待补录）";
            }
            else {
                String missingName = extractMissingBookName(requestText);
                if (StringUtils.hasText(missingName)) {
                    Book fuzzyResolved = resolveBook(null, missingName);
                    if (fuzzyResolved != null) {
                        finalBookId = String.valueOf(fuzzyResolved.getBookId());
                        displayTarget = fuzzyResolved.getBookName();
                    } else {
                        finalBookId = "MISSING:" + missingName;
                        displayTarget = missingName + "（待补录）";
                    }
                }
            }
            if (!StringUtils.hasText(finalBookId)) {
                return handled(agentName, userId, memoryId, requestText, start, "图书建议可直接写书名，例如：图书建议《xx》建议补充新版。");
            }

            Advice advice = new Advice();
            advice.setUserName(resolveUserName(userId, userName));
            advice.setBookId(finalBookId);
            advice.setInfo(adviceText);
            Integer rows = adviceService.inputSuggest(advice);
            boolean ok = rows != null && rows > 0;
            return handled(agentName, userId, memoryId, requestText, start, ok ? "图书建议提交成功：" + safe(displayTarget) : "图书建议提交失败。");
        } catch (Exception e) {
            return notHandled(agentName, userId, memoryId, requestText, start, e);
        }
    }

    public PolarisAgentRouteResult executeFeedbackAdviceAgent(String userName, Integer userId, Long memoryId, String requestText, String adviceText) {
        long start = System.currentTimeMillis();
        String agentName = "feedback_advice_agent";
        try {
            if (!StringUtils.hasText(adviceText)) return handled(agentName, userId, memoryId, requestText, start, "请补充反馈建议内容。");

            Advice advice = new Advice();
            advice.setUserName(resolveUserName(userId, userName));
            advice.setBookId(null);
            advice.setInfo(adviceText);
            Integer rows = adviceService.inputSuggest(advice);
            boolean ok = rows != null && rows > 0;
            return handled(agentName, userId, memoryId, requestText, start, ok ? "反馈建议提交成功。" : "反馈建议提交失败。");
        } catch (Exception e) {
            return notHandled(agentName, userId, memoryId, requestText, start, e);
        }
    }

    private boolean isFeaturedRecommendIntent(String text, Long memoryId) {
        if (text.contains("换一批") || text.contains("再来一批")) return memoryId != null && featuredOffsetByMemory.containsKey(memoryId);
        return hasAny(text, "特色推荐", "特色页", "特色页面", "推荐书", "推荐几本", "给我推荐");
    }

    private boolean isSystemSearchIntent(String text) {
        return hasAny(text, "系统图书库", "系统书库", "检索", "搜索", "查找") && text.contains("书");
    }

    private boolean isUserDataQueryIntent(String text) {
        return hasAny(text, "我的数据", "用户数据", "喜好表", "评分表", "我的喜好", "我的评分", "我的书库");
    }

    private boolean isPreferenceManageIntent(String text) {
        return hasAny(text, "喜好调整", "调整喜好", "不想看", "还行", "想看", "喜欢")
                || (text.contains("把") && (text.contains("想看") || text.contains("不想看") || text.contains("还行")));
    }

    private boolean isScoreManageIntent(String text) {
        if (isUserDataQueryIntent(text)) return false;
        boolean hasScoreVerb = hasAny(text, "打分", "评个分", "评为") || (text.contains("给") && (text.contains("评") || text.contains("评分")));
        boolean hasScoreValue = SCORE_VALUE_PATTERN.matcher(text).find();
        if (hasScoreVerb && hasScoreValue) return true;
        return text.contains("评分") && hasScoreValue && (text.contains("给") || text.contains("对") || text.contains("把"));
    }

    private boolean isBookSuggestionIntent(String text) {
        return hasAny(text, "图书建议", "缺书建议", "缺书", "补书", "上架建议", "建议补充");
    }

    private boolean isFeedbackAdviceIntent(String text) {
        return hasAny(text, "反馈建议", "提出建议", "系统建议", "产品建议")
                || (text.contains("建议") && !isBookSuggestionIntent(text));
    }

    private boolean hasAny(String text, String... keys) {
        for (String key : keys) if (text.contains(key)) return true;
        return false;
    }

    private String extractSearchKeyword(String text) {
        String name = extractBookName(text);
        if (StringUtils.hasText(name)) return name;
        return text.replace("检索系统图书库", "").replace("搜索系统图书库", "")
                .replace("检索图书库", "").replace("搜索图书库", "")
                .replace("查找", "").replace("检索", "").replace("搜索", "")
                .replace("图书库", "").replace("书库", "").trim();
    }

    private Integer extractBookId(String text) {
        Matcher m = BOOK_ID_PATTERN.matcher(text);
        if (m.find()) return Integer.parseInt(m.group(1));
        return null;
    }

    private String extractBookName(String text) {
        Matcher matcher = BOOK_NAME_IN_BRACKETS.matcher(text);
        if (matcher.find()) return matcher.group(1).trim();

        Matcher natural = NATURAL_SCORE_PATTERN.matcher(text);
        if (natural.find() && StringUtils.hasText(natural.group(1))) return natural.group(1).trim();

        Matcher givePattern = Pattern.compile("给\\s*([\\p{IsHan}A-Za-z0-9\\-·\\s]{1,40})").matcher(text);
        if (givePattern.find()) return givePattern.group(1).trim();
        return null;
    }

    private Integer extractScore(String text) {
        Matcher m = SCORE_PATTERN.matcher(text);
        if (m.find()) return Integer.parseInt(m.group(1));

        Matcher raw = Pattern.compile("\\b([1-5])\\b").matcher(text);
        if (raw.find()) return Integer.parseInt(raw.group(1));
        return null;
    }

    private Integer extractLikeLevel(String text) {
        Matcher m = LIKE_LEVEL_PATTERN.matcher(text);
        if (m.find()) return Integer.parseInt(m.group(1));
        if (text.contains("不想看")) return 0;
        if (text.contains("还行") || text.contains("一般")) return 1;
        if (text.contains("想看") || text.contains("喜欢")) return 2;
        return null;
    }

    private String extractAdviceText(String text) {
        String result = text.replace("图书建议", "").replace("缺书建议", "")
                .replace("反馈建议", "").replace("提出建议", "").trim();
        if (result.startsWith("：") || result.startsWith(":")) result = result.substring(1).trim();
        return result;
    }

    private String extractMissingBookName(String text) {
        String byBracket = extractBookName(text);
        if (StringUtils.hasText(byBracket)) return byBracket;

        Matcher byPossessive = Pattern.compile("([\\p{IsHan}A-Za-z0-9\\-·]{2,40})的").matcher(text);
        if (byPossessive.find()) return byPossessive.group(1).trim();

        String cleaned = text.replace("图书建议", "").replace("缺书建议", "")
                .replace("建议补充", "").replace("建议", "").replace("新版", "")
                .replace("特色页面", "").replace("页面", "").replace("图书数据", "")
                .replace("增加", "").replace("补充", "").replace("效果", "").replace("：", "")
                .replace(":", "").trim();
        Matcher nameMatcher = Pattern.compile("([\\p{IsHan}A-Za-z0-9\\-·]{2,40})").matcher(cleaned);
        if (nameMatcher.find()) return nameMatcher.group(1).trim();
        return null;
    }

    private Book resolveBook(Integer bookId, String bookName) {
        if (bookId != null) return bookService.getById(bookId);
        if (!StringUtils.hasText(bookName)) return null;

        String target = normalizeName(bookName);
        List<Book> books = bookService.searchBookByLike(bookName.trim());
        if (books == null || books.isEmpty()) return null;

        List<Book> exact = books.stream()
                .filter(b -> normalizeName(safe(b.getBookName())).equals(target))
                .collect(Collectors.toList());
        if (!exact.isEmpty()) return exact.get(0);

        List<Book> contains = books.stream()
                .filter(b -> normalizeName(safe(b.getBookName())).contains(target)
                        || target.contains(normalizeName(safe(b.getBookName()))))
                .sorted(Comparator.comparingInt(b -> Math.abs(normalizeName(safe(b.getBookName())).length() - target.length())))
                .collect(Collectors.toList());
        if (!contains.isEmpty()) return contains.get(0);
        return books.get(0);
    }

    private String resolveUserName(Integer userId, String userName) {
        if (StringUtils.hasText(userName)) return userName;
        if (userId == null) return "anonymous";
        User user = userService.getById(userId);
        if (user != null && StringUtils.hasText(user.getName())) return user.getName();
        return "user_" + userId;
    }

    private String percent(Double val) {
        double v = val == null ? 0D : val;
        return String.format("%.1f%%", v * 100D);
    }

    private double featureScore(FeatureDashboardBookItem item) {
        double accept = item.getAcceptRate() == null ? 0D : item.getAcceptRate();
        double complete = item.getCompleteRate() == null ? 0D : item.getCompleteRate();
        double duration = item.getAvgDurationMs() == null ? 0D : Math.min(item.getAvgDurationMs(), 300000L) / 300000D;
        return accept * 0.5 + complete * 0.3 + duration * 0.2;
    }

    private String safe(String text) {
        return StringUtils.hasText(text) ? text : "-";
    }

    private String likeLabel(Integer level) {
        if (level == null) return "-";
        if (level == 2) return "想看";
        if (level == 1) return "还行";
        return "不想看";
    }

    private String findFeaturePath(String bookName) {
        if (!StringUtils.hasText(bookName)) return null;
        String target = normalizeName(bookName);
        for (FeatureBookRoute item : FEATURE_BOOK_ROUTES) {
            String en = normalizeName(item.enTitle);
            String cn = normalizeName(item.cnTitle);
            if (target.equals(en) || target.equals(cn) || en.contains(target) || cn.contains(target) || target.contains(en) || target.contains(cn)) {
                return item.path;
            }
        }
        return null;
    }

    private String normalizeName(String text) {
        return text == null ? "" : text.toLowerCase().replaceAll("\\s+", "");
    }

    private PolarisAgentRouteResult handled(String agentName, Integer userId, Long memoryId, String requestText, long startMs, String response) {
        agentMetricService.record(agentName, userId, memoryId, requestText, true, System.currentTimeMillis() - startMs, "router", null);
        return new PolarisAgentRouteResult(true, agentName, response);
    }

    private PolarisAgentRouteResult notHandled(String agentName, Integer userId, Long memoryId, String requestText, long startMs, Exception e) {
        agentMetricService.record(agentName, userId, memoryId, requestText, false, System.currentTimeMillis() - startMs, "router", e.getMessage());
        return PolarisAgentRouteResult.notHandled();
    }

    public Map<String, Object> systemCatalogForTool(String keyword, Integer limit) {
        List<Book> books = StringUtils.hasText(keyword) ? bookService.searchBookByLike(keyword.trim()) : Collections.emptyList();
        int safeLimit = limit == null ? 5 : Math.max(1, Math.min(limit, 20));
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        for (Book b : books.stream().limit(safeLimit).collect(Collectors.toList())) {
            Map<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("bookId", b.getBookId());
            row.put("bookName", b.getBookName());
            row.put("bookAuthor", b.getBookAuthor());
            row.put("bookClassify", b.getBookClassify());
            row.put("featurePath", findFeaturePath(b.getBookName()));
            rows.add(row);
        }
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("keyword", keyword);
        result.put("items", rows);
        result.put("total", books.size());
        result.put("returned", rows.size());
        return result;
    }

    public Map<String, Object> userDataForTool(Integer userId, Integer limit) {
        int safeLimit = limit == null ? 10 : Math.max(1, Math.min(limit, 30));
        List<BookLike> likes = bookLikeService.list(new LambdaQueryWrapper<BookLike>().eq(BookLike::getUserId, userId));
        List<BookScore> scores = bookScoreService.list(new LambdaQueryWrapper<BookScore>().eq(BookScore::getUserId, userId));

        List<BookLike> limitedLikes = likes.stream().limit(safeLimit).collect(Collectors.toList());
        List<BookScore> limitedScores = scores.stream().limit(safeLimit).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("userId", userId);
        result.put("likeCount", likes.size());
        result.put("scoreCount", scores.size());
        result.put("likes", limitedLikes);
        result.put("scores", limitedScores);
        result.put("returnedLikeCount", limitedLikes.size());
        result.put("returnedScoreCount", limitedScores.size());
        return result;
    }

    private static class FeatureBookRoute {
        private final String enTitle;
        private final String cnTitle;
        private final String path;
        private FeatureBookRoute(String enTitle, String cnTitle, String path) {
            this.enTitle = enTitle;
            this.cnTitle = cnTitle;
            this.path = path;
        }
    }
}
