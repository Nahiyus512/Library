//创建一个路由器，并且暴露出去
import {createRouter,createWebHistory,createWebHashHistory} from "vue-router";

import Login from "../components/Login.vue"
import Home from "../components/Home.vue"
import MainPage from "../components/MainPage.vue"
import UserIndex from "../components/UserIndex.vue"
import Book from "../components/Book.vue"
import BookRecommend from "../components/BookRecommend.vue"
import Suggest from "../components/Suggest.vue"
import User from "../components/User.vue"
import Admin from "../views/Admin.vue"
import BookInfo from "../views/BookInfo.vue"
import UserInfo from "../views/UserInfo.vue"
import SystemInfo from "../views/SystemInfo.vue"
import BookBorrowInfo from "../views/BookBorrowInfo.vue"
import Advice from "../views/Advice.vue"
import BookClass from "../views/BookClass.vue";
import CategoryPlaceholder from "../views/CategoryPlaceholder.vue";
import ArtPlaceholder from "../views/ArtPlaceholder.vue";
import AmusingOurselvesToDeath from "../views/book/AmusingOurselvesToDeath.vue";
import BraveNewWorld from "../views/book/BraveNewWorld.vue";
import Dune from "../views/book/Dune.vue";
import GridSystems from "../views/book/GridSystems.vue";
import HitchhikersGuide from "../views/book/HitchhikersGuide.vue";
import InteractionOfColor from "../views/book/InteractionOfColor.vue";
import Life30 from "../views/book/Life30.vue";
import NineteenEightyFour from "../views/book/NineteenEightyFour.vue";
import NonDesignersDesignBook from "../views/book/NonDesignersDesignBook.vue";
import Sapiens from "../views/book/Sapiens.vue";
import ThreeBody from "../views/book/ThreeBody.vue";
import Zen from "../views/book/Zen.vue";


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
            component:Home,
            redirect:"/main",
            children:[
                {
                    path: "/main",
                    name: "main",
                    component: MainPage
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
                    component: Book
                },

                {
                    path: "/bookRecommend",
                    name: "bookRecommend",
                    component: BookRecommend
                },
                {
                    path: "/user",
                    name: "user",
                    component: User
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
            component:Admin,
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
        {
            path:"/",
            component:Login
        }
    ]
})


export default router
