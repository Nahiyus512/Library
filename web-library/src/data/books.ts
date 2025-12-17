export interface BookItem {
  id: number;
  title: string;
  titleCN: string;
  author: string;
  quote: string;
  categories: string[];
  colorTheme: string;
  textColor?: string;
  isBorder?: boolean;
  path: string;
  widgetType?: 'bookshelf' | 'radio';
}

export const books: BookItem[] = [
  { 
    id: 1, 
    title: "Three-Body Problem", 
    titleCN: "三体", 
    quote: "给岁月以文明，而不是给文明以岁月。", 
    author: "刘慈欣", 
    categories: ["科幻"], 
    colorTheme: "#000000",
    textColor: "#FFFFFF",
    path: '/book/ThreeBody'
  }, 
  { 
    id: 2, 
    title: "Sapiens: A Brief History of Humankind", 
    titleCN: "人类简史", 
    quote: "历史从无正义。", 
    author: "尤瓦尔·赫拉利", 
    categories: ["哲学", "历史"], 
    colorTheme: "#f0f0e6",
    textColor: "#000000",
    path: '/book/Sapiens'
  }, 
  { 
    id: 3, 
    title: "The Hitchhiker's Guide to the Galaxy", 
    titleCN: "银河系漫游指南", 
    quote: "DON'T PANIC.", 
    author: "道格拉斯·亚当斯", 
    categories: ["科幻"], 
    colorTheme: "#000000",
    textColor: "#00FF00",
    path: '/book/HitchhikersGuide'
  }, 
  { 
    id: 4, 
    title: "Interaction of Color", 
    titleCN: "色彩互动学", 
    quote: "色彩是相对的。", 
    author: "约瑟夫·阿尔伯斯", 
    categories: ["知识"], 
    colorTheme: "#f5f5f5",
    textColor: "#000000",
    path: '/book/InteractionOfColor'
  }, 
  { 
    id: 5, 
    title: "The Non-Designer's Design Book", 
    titleCN: "写给大家看的设计书", 
    quote: "亲密性、对齐、重复、对比。", 
    author: "罗宾·威廉姆斯", 
    categories: ["知识"], 
    colorTheme: "#f5f5f7", 
    textColor: "#000000",
    isBorder: true,
    path: '/book/NonDesignersDesignBook'
  }, 
  { 
    id: 6, 
    title: "Grid Systems in Graphic Design", 
    titleCN: "平面设计中的网格系统", 
    quote: "秩序是自由的基础。", 
    author: "约瑟夫·米勒-布罗克曼", 
    categories: ["知识"], 
    colorTheme: "#f4f4f0",
    textColor: "#000000",
    path: '/book/GridSystems'
  }, 
  { 
    id: 7, 
    title: "Dune", 
    titleCN: "沙丘", 
    quote: "恐惧是思维的杀手。", 
    author: "弗兰克·赫伯特", 
    categories: ["科幻"], 
    colorTheme: "#7B3306",
    textColor: "#FFD700",
    path: '/book/Dune'
  }, 
  { 
    id: 8, 
    title: "1984", 
    titleCN: "1984", 
    quote: "老大哥在看着你。", 
    author: "乔治·奥威尔", 
    categories: ["科幻"], 
    colorTheme: "#050505",
    textColor: "#028b3bff",
    path: '/book/NineteenEightyFour'
  }, 
  { 
    id: 9, 
    title: "Brave New World", 
    titleCN: "美丽新世界", 
    quote: "共有•统一•安定。", 
    author: "阿道司·赫胥黎", 
    categories: ["科幻"], 
    colorTheme: "#000000ff",
    textColor: "#61dafbff",
    path: '/book/BraveNewWorld'
  },
  { 
    id: 10, 
    title: "Zen and the Art of Motorcycle Maintenance", 
    titleCN: "禅与摩托车维修艺术", 
    quote: "关心即是质量。", 
    author: "罗伯特·波西格", 
    categories: ["哲学", "生活"], 
    colorTheme: "#047857",
    textColor: "#FFFFFF",
    path: '/book/Zen'
  }, 
  { 
    id: 11, 
    title: "Amusing Ourselves to Death", 
    titleCN: "娱乐至死", 
    quote: "我们将毁于我们所热爱的东西。", 
    author: "尼尔·波兹曼", 
    categories: ["哲学", "生活"], 
    colorTheme: "#121212",
    textColor: "#FFFFFF",
    path: '/book/AmusingOurselvesToDeath'
  }, 
  { 
    id: 12, 
    title: "Life 3.0", 
    titleCN: "生命3.0", 
    quote: "生命是自我复制的信息处理系统。", 
    author: "迈克斯·泰格马克", 
    categories: ["科幻"], 
    colorTheme: "#000000",
    textColor: "#FFFFFF",
    path: '/book/Life30'
  }, 
  { 
    id: 13, 
    title: "Journey to the West", 
    titleCN: "西游记", 
    quote: "心生，种种魔生；心灭，种种魔灭。", 
    author: "吴承恩", 
    categories: ["经典"], 
    colorTheme: "#FFD700",
    textColor: "#ff0000ff",
    path: '/book/JourneyToTheWest'
  },
  { 
    id: 14, 
    title: "Three Kingdoms", 
    titleCN: "三国演义", 
    quote: "天下大势，分久必合，合久必分。", 
    author: "罗贯中", 
    categories: ["经典"], 
    colorTheme: "#8E2323",
    textColor: "#ffffffff",
    path: '/book/ThreeKingdoms'
  }
];
