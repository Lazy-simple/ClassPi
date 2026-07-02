package com.classpi.controller;

import com.classpi.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiEvaluateController {

    @PostMapping("/evaluate")
    public Result evaluate(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String prompt = request.get("prompt");

        if (content == null || content.trim().isEmpty()) {
            return Result.error("作业内容不能为空");
        }

        // 生成模拟评价
        String response = generateMockEvaluation(content);
        return Result.success("AI评价成功", response);
    }

    private String generateMockEvaluation(String content) {
        String lowerContent = content.toLowerCase();

        String score = "85";
        String level = "良好";
        String suggestion = "可以进一步深化论述，增加更多具体案例。";

        if (lowerContent.contains("代码") || lowerContent.contains("java") || lowerContent.contains("python")) {
            score = "82";
            level = "良好";
            suggestion = "代码结构清晰，但建议增加更多注释，注意代码规范。";
        } else if (lowerContent.contains("论文") || lowerContent.contains("报告")) {
            score = "88";
            level = "优秀";
            suggestion = "论述充分，逻辑清晰，建议进一步完善参考文献格式。";
        } else if (lowerContent.contains("设计") || lowerContent.contains("方案")) {
            score = "80";
            level = "良好";
            suggestion = "方案设计合理，建议考虑更多边界情况。";
        } else if (lowerContent.length() < 20) {
            score = "70";
            level = "一般";
            suggestion = "内容较简略，建议补充更多细节和论证。";
        }

        return "📝 **AI 作业评价**\n\n" +
                "**完成度：** " + level + "，作业内容基本完整。\n\n" +
                "**内容质量：** 思路清晰，能体现出对知识点的理解。\n\n" +
                "**格式规范：** 格式基本规范。\n\n" +
                "**优点：** 能够结合实际进行分析，有一定的思考深度。\n\n" +
                "**改进建议：** " + suggestion + "\n\n" +
                "**评分：** " + score + "/100 分\n\n" +
                "💪 继续加油！";
    }
}