import request from '@/utils/request';

export const getTeacherPreparations = (teacherId, page = 1, pageSize = 10) => {
  return request({
    url: `/preparation/teacher/${teacherId}`,
    method: 'get',
    params: { page, pageSize }
  });
};

export const getTeacherPreparationsByType = (teacherId, type, page = 1, pageSize = 10) => {
  return request({
    url: `/preparation/teacher/${teacherId}/type/${type}`,
    method: 'get',
    params: { page, pageSize }
  });
};

export const getUnassignedPreparations = (teacherId, page = 1, pageSize = 10) => {
  return request({
    url: `/preparation/teacher/${teacherId}/unassigned`,
    method: 'get',
    params: { page, pageSize }
  });
};

export const addPreparation = (data) => {
  return request({
    url: '/preparation/add',
    method: 'post',
    data
  });
};

export const updatePreparation = (id, data) => {
  return request({
    url: `/preparation/${id}`,
    method: 'put',
    params: data
  });
};

export const deletePreparation = (id, teacherId, identity) => {
  return request({
    url: `/preparation/${id}`,
    method: 'delete',
    params: { teacherId, identity }
  });
};

export const getPreparationById = (id, teacherId, identity) => {
  return request({
    url: `/preparation/${id}`,
    method: 'get',
    params: { teacherId, identity }
  });
};

export const assignToCourse = (id, courseId, courseNo, teacherId, identity) => {
  return request({
    url: `/preparation/${id}/assign`,
    method: 'put',
    params: { courseId, courseNo, teacherId, identity }
  });
};
