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
            path:"/",
            component:Login
        }
    ]
})


export default router
