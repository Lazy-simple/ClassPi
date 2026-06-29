import request from '@/utils/request';

export const getTeacherPreparations = (teacherId, page = 1, pageSize = 10) => {
  return request({
    url: `/preparation/teacher/${String(teacherId)}`,
    method: 'get',
    params: { page, pageSize }
  });
};

export const getTeacherPreparationsByType = (teacherId, type, page = 1, pageSize = 10) => {
  return request({
    url: `/preparation/teacher/${String(teacherId)}/type/${type}`,
    method: 'get',
    params: { page, pageSize }
  });
};

export const getUnassignedPreparations = (teacherId, page = 1, pageSize = 10) => {
  return request({
    url: `/preparation/teacher/${String(teacherId)}/unassigned`,
    method: 'get',
    params: { page, pageSize }
  });
};

export const addPreparation = (data) => {
  return request({
    url: '/preparation/add',
    method: 'post',
    data: { ...data, teacherId: String(data.teacherId) }
  });
};

export const updatePreparation = (id, data) => {
  return request({
    url: `/preparation/${id}`,
    method: 'put',
    params: { ...data, teacherId: String(data.teacherId) }
  });
};

export const deletePreparation = (id, teacherId, identity) => {
  return request({
    url: `/preparation/${id}`,
    method: 'delete',
    params: { teacherId: String(teacherId), identity }
  });
};

export const getPreparationById = (id, teacherId, identity) => {
  return request({
    url: `/preparation/${id}`,
    method: 'get',
    params: { teacherId: String(teacherId), identity }
  });
};

export const assignToCourse = (id, courseId, courseNo, teacherId, identity) => {
  return request({
    url: `/preparation/${id}/assign`,
    method: 'put',
    params: { courseId, courseNo, teacherId: String(teacherId), identity }
  });
};
