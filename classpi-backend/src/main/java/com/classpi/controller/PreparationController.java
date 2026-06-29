package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.ImportPreparationDTO;
import com.classpi.dto.PreparationCreateDTO;
import com.classpi.service.PreparationService;
import com.classpi.security.RequireRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/preparation")
@CrossOrigin
public class PreparationController {

    @Autowired
    private PreparationService preparationService;

    @PostMapping("/add")
    @RequireRole({"teacher"})
    public Result addPreparation(@RequestBody @Validated PreparationCreateDTO dto) {
        return preparationService.addPreparation(
            dto.getTeacherId(),
            dto.getTeacherName(),
            dto.getTitle(),
            dto.getType(),
            dto.getContent()
        );
    }

    @DeleteMapping("/{id}")
    public Result deletePreparation(@PathVariable Integer id,
                                     @RequestParam String teacherId,
                                     @RequestParam String identity) {
        return preparationService.deletePreparation(id, teacherId, identity);
    }

    @PutMapping("/{id}")
    public Result updatePreparation(@PathVariable Integer id,
                                     @RequestParam String title,
                                     @RequestParam String content,
                                     @RequestParam String teacherId,
                                     @RequestParam String identity) {
        return preparationService.updatePreparation(id, title, content, teacherId, identity);
    }

    @GetMapping("/teacher/{teacherId}")
    public Result getTeacherPreparations(@PathVariable String teacherId,
                                          @RequestParam(defaultValue = "1") Long page,
                                          @RequestParam(defaultValue = "10") Long pageSize) {
        return preparationService.getTeacherPreparations(teacherId, page, pageSize);
    }

    @GetMapping("/teacher/{teacherId}/type/{type}")
    public Result getTeacherPreparationsByType(@PathVariable String teacherId,
                                                @PathVariable String type,
                                                @RequestParam(defaultValue = "1") Long page,
                                                @RequestParam(defaultValue = "10") Long pageSize) {
        return preparationService.getTeacherPreparationsByType(teacherId, type, page, pageSize);
    }

    @GetMapping("/teacher/{teacherId}/unassigned")
    public Result getUnassignedPreparations(@PathVariable String teacherId,
                                             @RequestParam(defaultValue = "1") Long page,
                                             @RequestParam(defaultValue = "10") Long pageSize) {
        return preparationService.getUnassignedPreparations(teacherId, page, pageSize);
    }

    @PutMapping("/{id}/assign")
    public Result assignToCourse(@PathVariable Integer id,
                                  @RequestParam Integer courseId,
                                  @RequestParam String courseNo,
                                  @RequestParam String teacherId,
                                  @RequestParam String identity) {
        return preparationService.assignToCourse(id, courseId, courseNo, teacherId, identity);
    }

    @GetMapping("/{id}")
    public Result getPreparationById(@PathVariable Integer id,
                                      @RequestParam String teacherId,
                                      @RequestParam String identity) {
        return preparationService.getPreparationById(id, teacherId, identity);
    }

    /**
     * 从备课区导入资源到课程
     * POST /preparation/import/resource
     */
    @PostMapping("/import/resource")
    public Result importResource(@RequestBody ImportPreparationDTO dto, @RequestHeader("userId") String userId) {
        return preparationService.importResource(dto, userId);
    }

    /**
     * 从备课区导入作业到课程
     * POST /preparation/import/homework
     */
    @PostMapping("/import/homework")
    public Result importHomework(@RequestBody ImportPreparationDTO dto, @RequestHeader("userId") String userId) {
        return preparationService.importHomework(dto, userId);
    }

    /**
     * 从备课区导入话题到课程
     * POST /preparation/import/topic
     */
    @PostMapping("/import/topic")
    public Result importTopic(@RequestBody ImportPreparationDTO dto, @RequestHeader("userId") String userId) {
        return preparationService.importTopic(dto, userId);
    }
}
