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
import ZenAndMotorcycle from "../views/book/Zen.vue";
import ThreeBodyProblem from "../views/book/ThreeBody.vue";
import Sapiens from "../views/book/Sapiens.vue";
import HitchhikersGuide from "../views/book/HitchhikersGuide.vue";
import InteractionOfColor from "../views/book/InteractionOfColor.vue";
import NonDesignersDesignBook from "../views/book/NonDesignersDesignBook.vue";
import GridSystems from "../views/book/GridSystems.vue";
import Dune from "../views/book/Dune.vue";
import Book1984 from "../views/book/1984.vue";
import BraveNewWorld from "../views/book/BraveNewWorld.vue";
import AmusingOurselvesToDeath from "../views/book/AmusingOurselvesToDeath.vue";
import Life30 from "../views/book/Life30.vue";


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
        {
            path:"/book/zen-motorcycle",
            name:"ZenAndMotorcycle",
            component:ZenAndMotorcycle
        },
        { path: "/book/three-body", name: "ThreeBodyProblem", component: ThreeBodyProblem },
        { path: "/book/sapiens", name: "Sapiens", component: Sapiens },
        { path: "/book/hitchhikers-guide", name: "HitchhikersGuide", component: HitchhikersGuide },
        { path: "/book/interaction-of-color", name: "InteractionOfColor", component: InteractionOfColor },
        { path: "/book/non-designers-design-book", name: "NonDesignersDesignBook", component: NonDesignersDesignBook },
        { path: "/book/grid-systems", name: "GridSystems", component: GridSystems },
        { path: "/book/dune", name: "Dune", component: Dune },
        { path: "/book/1984", name: "Book1984", component: Book1984 },
        { path: "/book/brave-new-world", name: "BraveNewWorld", component: BraveNewWorld },
        { path: "/book/amusing-ourselves-to-death", name: "AmusingOurselvesToDeath", component: AmusingOurselvesToDeath },
        { path: "/book/life-30", name: "Life30", component: Life30 },
        {
            path:"/",
            component:Login
        }
    ]
})


export default router
