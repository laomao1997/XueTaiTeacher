package com.xuetai.teacher.xuetaiteacher.constant;

/**
 * 接口方法
 * Created by XinSheng on 16/9/23.
 */

public class MethodCode {
    //登录
    public static final String Login = "Teacher/Entry.login";
    //注册
    public static final String Register = "Teacher/Entry.register";
    //忘记密码
    public static final String ForgetPassword = "Teacher/Entry.forgetPassword";
    //短信验证码
    public static final String SmsCode = "Common/Message.sendSmsCode";
    //上传文件
    public static final String UPLOADIMAGE = "Common/File.uploadEncoded";
    //更新信息
    public static final String UpDateInfo = "Teacher/Info.update";
    //获取阶段年级科目信息
    public static final String ListLevelGradeSubject = "Common/Config.listLevelGradeSubject";
    //查询老师审核状态
    public static final String ExamineStatus = "Teacher/Info.examineStatus";
    //初始化老师收入信息
    public static final String TeacherIncome = "Teacher/Info.balance";
    //收入明细
    public static final String IncomeDetail = "Teacher/Info.incomeList";
    //获取老师一对一课表时间详情
    public static final String TeacherInfoCalendar = "Teacher/Time.timeList";
    //提现
    public static final String WithdrawApply = "Teacher/DrawApply.addApply";
    //增加提现账号
    public static final String AddTeacherAccountNum = "Teacher/Info.updatePayAccount";
    //课程详细信息
    public static final String CourseDetailInitial = "Teacher/Course.detail";
    //修改教师一对一课表时间
    public static final String UpdateTeacherFreeTime = "Teacher/Time.updateFreeTime";
    //退出登录
    public static final String Logout = "Teacher/Entry.logout";
    //获取教科书版本
    public static final String TextBookVersionList = "Teacher/Question.textbookVersionList";
    //获取总复习详细内容
    public static final String QuestionPointList = "Teacher/Question.pointList";
    //获取教科书年级列表
    public static final String QuestionGradeList = "Teacher/Question.gradeList";
    //获取教科书具体章节
    public static final String QuestionChapterList = "Teacher/Question.chapterList";
    //获取聊天列表
    public static final String GetDialogues = "Common/Dialogue.teacherList";
    //获取聊天详情
    public static final String GetChartDetaile = "Common/Dialogue.messages";
    //发送聊天信息
    public static final String SendDialoguesMessage = "Common/Dialogue.sendMessage";
    //获取教师评价
    public static final String GetComment = "Common/Comment.all";
    //查看课程内容
    public static final String SelectedPointTree = "Teacher/Question.selectedPointTree";
    //保存备课信息
    public static final String SaveCoach = "Teacher/Course.saveCoach";

}
