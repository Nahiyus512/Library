//创建一个路由器，并且暴露出去
import {createRouter,createWebHistory} from "vue-router";

// Layouts
import ClientLayout from "@/layouts/ClientLayout.vue"
import AdminLayout from "@/layouts/AdminLayout.vue"

// Auth
import Login from "@/views/auth/Login.vue"

// Client Views
import MainPage from "@/views/client/MainPage.vue"
import UserIndex from "@/views/client/UserIndex.vue"
import BookList from "@/views/client/BookList.vue"
import BookRecommend from "@/views/client/BookRecommend.vue"
import Suggest from "@/views/client/Suggest.vue"
import UserCenter from "@/views/client/UserCenter.vue"
import CategoryPlaceholder from "@/views/client/CategoryPlaceholder.vue"
import ArtPlaceholder from "@/views/client/ArtPlaceholder.vue"

// Category Views
import ClassicCategory from "@/views/categories/ClassicCategory.vue";
import PhilosophyCategory from "@/views/categories/PhilosophyCategory.vue";
import KnowledgeCategory from "@/views/categories/KnowledgeCategory.vue";
import HistoryCategory from "@/views/categories/HistoryCategory.vue";
import LifeCategory from "@/views/categories/LifeCategory.vue";
import SciFiCategory from "@/views/categories/SciFiCategory.vue";

// Admin Views
import BookInfo from "@/views/admin/BookInfo.vue"
import UserInfo from "@/views/admin/UserInfo.vue"
import SystemInfo from "@/views/admin/SystemInfo.vue"
import BookBorrowInfo from "@/views/admin/BookBorrowInfo.vue"
import Advice from "@/views/admin/Advice.vue"
import BookClass from "@/views/admin/BookClass.vue";

// Book Content Views
import AmusingOurselvesToDeath from "@/views/books/AmusingOurselvesToDeath.vue";
import BraveNewWorld from "@/views/books/BraveNewWorld.vue";
import Dune from "@/views/books/Dune.vue";
import GridSystems from "@/views/books/GridSystems.vue";
import HitchhikersGuide from "@/views/books/HitchhikersGuide.vue";
import InteractionOfColor from "@/views/books/InteractionOfColor.vue";
import Life30 from "@/views/books/Life30.vue";
import NineteenEightyFour from "@/views/books/NineteenEightyFour.vue";
import NonDesignersDesignBook from "@/views/books/NonDesignersDesignBook.vue";
import Sapiens from "@/views/books/Sapiens.vue";
import ThreeBody from "@/views/books/ThreeBody.vue";
import Zen from "@/views/books/Zen.vue";
import JourneyToTheWest from "@/views/books/JourneyToTheWest.vue";
import ThreeKingdoms from "@/views/books/ThreeKingdoms.vue";


const router = createRouter({
    history:createWebHistory(),
    routes:[
        {
            path:"/login",
            name:"login",
            component:Login
        },
        {
            path:"/home",
            name:"home",
            component:ClientLayout,
            redirect:"/main",
            children:[
                {
                    path: "/main",
                    name: "main",
                    component: MainPage
                },
                {
                    path: "/category/经典",
                    name: "category-classic",
                    component: ClassicCategory
                },
                {
                    path: "/category/哲学",
                    name: "category-philosophy",
                    component: PhilosophyCategory
                },
                {
                    path: "/category/知识",
                    name: "category-knowledge",
                    component: KnowledgeCategory
                },
                {
                    path: "/category/历史",
                    name: "category-history",
                    component: HistoryCategory
                },
                {
                    path: "/category/生活",
                    name: "category-life",
                    component: LifeCategory
                },
                {
                    path: "/category/科幻",
                    name: "category-scifi",
                    component: SciFiCategory
                },
                {
                    path: "/category/:id",
                    name: "category",
                    component: CategoryPlaceholder
                },
                {
                    path: "/art/:id",
                    name: "art",
                    component: ArtPlaceholder
                },
                {
                    path: "/userIndex",
                    name: "UserIndex",
                    component: UserIndex
                },
                {
                    path: "/book",
                    name: "book",
                    component: BookList
                },

                {
                    path: "/bookRecommend",
                    name: "bookRecommend",
                    component: BookRecommend
                },
                {
                    path: "/user",
                    name: "user",
                    component: UserCenter
                },
                {
                    path: "/suggest",
                    name: "suggest",
                    component: Suggest
                }
            ]
        },
        {
            path:"/admin",
            name:"admin",
            component:AdminLayout,
            redirect:"/userInfo",
            children:[
                {
                    path:"/userInfo",
                    name:"userInfo",
                    component:UserInfo
                },
                {
                    path:"/bookInfo",
                    name:"bookInfo",
                    component:BookInfo
                },
                {
                    path: "/bookClass",
                    name: "bookClass",
                    component: BookClass
                },
                {
                    path:"/sysInfo",
                    name:"sysInfo",
                    component:SystemInfo
                },
                {
                    path:"/advice",
                    name:"advice",
                    component:Advice
                },
                {
                    path:"/bookBorrowInfo",
                    name:"bookBorrowInfo",
                    component:BookBorrowInfo
                },
                {
                    path:"/admin",
                    component:UserInfo
                }
            ]
        },
        { path: "/book/AmusingOurselvesToDeath", name: "AmusingOurselvesToDeath", component: AmusingOurselvesToDeath },
        { path: "/book/BraveNewWorld", name: "BraveNewWorld", component: BraveNewWorld },
        { path: "/book/Dune", name: "Dune", component: Dune },
        { path: "/book/GridSystems", name: "GridSystems", component: GridSystems },
        { path: "/book/HitchhikersGuide", name: "HitchhikersGuide", component: HitchhikersGuide },
        { path: "/book/InteractionOfColor", name: "InteractionOfColor", component: InteractionOfColor },
        { path: "/book/Life30", name: "Life30", component: Life30 },
        { path: "/book/NineteenEightyFour", name: "NineteenEightyFour", component: NineteenEightyFour },
        { path: "/book/NonDesignersDesignBook", name: "NonDesignersDesignBook", component: NonDesignersDesignBook },
        { path: "/book/Sapiens", name: "Sapiens", component: Sapiens },
        { path: "/book/ThreeBody", name: "ThreeBody", component: ThreeBody },
        { path: "/book/Zen", name: "Zen", component: Zen },
        { path: "/book/JourneyToTheWest", name: "JourneyToTheWest", component: JourneyToTheWest },
        { path: "/book/ThreeKingdoms", name: "ThreeKingdoms", component: ThreeKingdoms },
        {
            path:"/",
            component:Login
        }
    ]
})


export default router