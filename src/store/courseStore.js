// src/store/courseStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useCourseStore = defineStore('course', () => {
  // 1. 定义课程列表状态 (使用 ref 以便在组件中直接修改)
  const courses = ref([
    {
      id: 1,
      name: '高等数学 (上)',
      teacher: '张教授',
      semester: '2023-2024 第一学期',
      coverImage: 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80', // 临时网络图
      progress: 75
    },
    {
      id: 2,
      name: '大学英语 III',
      teacher: '李老师',
      semester: '2023-2024 第一学期',
      coverImage: 'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80',
      progress: 40
    },
    {
      id: 3,
      name: '计算机导论',
      teacher: '王博士',
      semester: '2023-2024 第一学期',
      coverImage: 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80',
      progress: 90
    }
  ])

  // 2. 添加课程的 Action (模拟)
  const addCourse = (newCourse) => {
    courses.value.push({
      id: Date.now(), // 生成临时ID
      ...newCourse,
      progress: 0,
      coverImage: newCourse.coverImage || 'https://via.placeholder.com/300x160'
    })
  }

  return { courses, addCourse }
})