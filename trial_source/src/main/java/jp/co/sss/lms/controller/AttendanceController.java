package jp.co.sss.lms.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.lms.dto.AttendanceManagementDto;
import jp.co.sss.lms.dto.LoginUserDto;
import jp.co.sss.lms.entity.TStudentAttendance;
import jp.co.sss.lms.form.AttendanceForm;
import jp.co.sss.lms.mapper.TStudentAttendanceMapper;
import jp.co.sss.lms.service.StudentAttendanceService;
import jp.co.sss.lms.util.AttendanceUtil;
import jp.co.sss.lms.util.Constants;

/**
 * 勤怠管理コントローラ
 * 
 * @author 東京ITスクール
 */
@Controller
@RequestMapping("/attendance")
public class AttendanceController {

	@Autowired
	private StudentAttendanceService studentAttendanceService;
	@Autowired
	private LoginUserDto loginUserDto;
	@Autowired
	private TStudentAttendanceMapper tStudentAttendanceMapper;

	/**
	 * 
	 * 勤怠管理画面 初期表示(Task25 過去日未入力チェック)
	 * 
	 * @param model
	 * @param fmt
	 * @param trainingStartTime
	 * @param trainingEndTime
	 * @param lmsUserId
	 * @return
	 */
	@RequestMapping(path = "/detail", method = RequestMethod.GET)
	public String indexCheck(Model model, String fmt, @Param("trainingStartTime") String trainingStartTime,
			@Param("trainingEndTime") String trainingEndTime, @Param("lmsUserId") Integer lmsUserId) {
		
		// 勤怠一覧の取得
		List<AttendanceManagementDto> attendanceManagementDtoList = studentAttendanceService
				.getAttendanceManagement(loginUserDto.getCourseId(), loginUserDto.getLmsUserId());
		model.addAttribute("attendanceManagementDtoList", attendanceManagementDtoList);

		//SimpleDateFormatクラスでフォーマットパターンを設定
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");
		//現在の日付を取得
		Date now = new Date();
		//日付を文字列に設定（要らないかも）
		sdf.format(now);

		//API呼び出し(未入力件数取得)
		Integer countNull = tStudentAttendanceMapper.findCountNull(trainingStartTime, trainingEndTime);
		
		//LMSユーザID・削除フラグ・現在日付を取得
		TStudentAttendance i = tStudentAttendanceMapper.selectById(lmsUserId);
		if (i != null) {
			model.addAttribute("deleteFlg", i.getDeleteFlg());
		}
		
		model.addAttribute("lmsUserId", loginUserDto.getLmsUserId());
		model.addAttribute("now", now);
		
		if (countNull != null) {
			
//			//true：未入力確認ダイアログをJavaScriptで表示
			model.addAttribute("notEnterFlg", true);
			
			//未入力がない場合(countNull == 0)
		} else {
			model.addAttribute("notEnterFlg", false);
		}

		return "attendance/detail";
	}


	/**
	 * 勤怠管理画面 『出勤』ボタン押下
	 * 
	 * @param model
	 * @return 勤怠管理画面
	 */
	@RequestMapping(path = "/detail", params = "punchIn", method = RequestMethod.POST)
	public String punchIn(Model model) {

		// 更新前のチェック
		String error = studentAttendanceService.punchCheck(Constants.CODE_VAL_ATWORK);
		model.addAttribute("error", error);
		// 勤怠登録
		if (error == null) {
			String message = studentAttendanceService.setPunchIn();
			model.addAttribute("message", message);
		}
		// 一覧の再取得
		List<AttendanceManagementDto> attendanceManagementDtoList = studentAttendanceService
				.getAttendanceManagement(loginUserDto.getCourseId(), loginUserDto.getLmsUserId());
		model.addAttribute("attendanceManagementDtoList", attendanceManagementDtoList);

		return "attendance/detail";
	}

	/**
	 * 勤怠管理画面 『退勤』ボタン押下
	 * 
	 * @param model
	 * @return 勤怠管理画面
	 */
	@RequestMapping(path = "/detail", params = "punchOut", method = RequestMethod.POST)
	public String punchOut(Model model) {

		// 更新前のチェック
		String error = studentAttendanceService.punchCheck(Constants.CODE_VAL_LEAVING);
		model.addAttribute("error", error);
		// 勤怠登録
		if (error == null) {
			String message = studentAttendanceService.setPunchOut();
			model.addAttribute("message", message);
		}
		// 一覧の再取得
		List<AttendanceManagementDto> attendanceManagementDtoList = studentAttendanceService
				.getAttendanceManagement(loginUserDto.getCourseId(), loginUserDto.getLmsUserId());
		model.addAttribute("attendanceManagementDtoList", attendanceManagementDtoList);

		return "attendance/detail";
	}

	/**
	 * 勤怠管理画面 『勤怠情報を直接編集する』リンク押下
	 * Task26
	 * 
	 * @param model
	 * @return 勤怠情報直接変更画面
	 */
	@RequestMapping(path = "/update")
	public String update(Model model) {
		
		// 勤怠管理リストの取得
		List<AttendanceManagementDto> attendanceManagementDtoList = studentAttendanceService
				.getAttendanceManagement(loginUserDto.getCourseId(), loginUserDto.getLmsUserId());
		
		LinkedHashMap<Integer, String> hour = AttendanceUtil.getTrainingTimeHours();
		LinkedHashMap<Integer, String> min = AttendanceUtil.getTrainingTimeMinutes();
		
		// 勤怠フォームの生成
		AttendanceForm attendanceForm = studentAttendanceService
				.setAttendanceForm(attendanceManagementDtoList);
		
		attendanceForm.setTrainingTimeHours(hour);
		attendanceForm.setTrainingTimeMinutes(min);
		
		model.addAttribute("attendanceForm", attendanceForm);
		// 選択肢を追加
		model.addAttribute("trainingTimeHours", hour);
		model.addAttribute("trainingTimeMinutes", min);
//		// 日次の勤怠フォームの作成？
//		DailyAttendanceForm dailyAttendanceForm = studentAttendanceService
//				.setDailyAttendanceForm();

		return "attendance/update";
	}

	/**
	 * 勤怠情報直接変更画面 『更新』ボタン押下
	 * 
	 * @param attendanceForm
	 * @param model
	 * @param result
	 * @return 勤怠管理画面
	 * @throws ParseException
	 */
	@RequestMapping(path = "/update", params = "complete", method = RequestMethod.POST)
	public String complete(AttendanceForm attendanceForm, Model model, BindingResult result)
			throws ParseException {

		// 更新
		String message = studentAttendanceService.update(attendanceForm);
		model.addAttribute("message", message);
		// 一覧の再取得
		List<AttendanceManagementDto> attendanceManagementDtoList = studentAttendanceService
				.getAttendanceManagement(loginUserDto.getCourseId(), loginUserDto.getLmsUserId());
		model.addAttribute("attendanceManagementDtoList", attendanceManagementDtoList);

		return "attendance/detail";
	}

}