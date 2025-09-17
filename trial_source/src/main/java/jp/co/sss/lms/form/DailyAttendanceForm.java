package jp.co.sss.lms.form;

import lombok.Data;

/**
 * 日次の勤怠フォーム
 * 
 * @author 東京ITスクール
 */
@Data
public class DailyAttendanceForm {

	/** 受講生勤怠ID */
	private Integer studentAttendanceId;
	/** 途中退校日 */
	private String leaveDate;
	/** 日付 */
	private String trainingDate;
	/** 出勤時間 */
	private String trainingStartTime;
	/** 退勤時間 */
	private String trainingEndTime;
	/** 中抜け時間 */
	private Integer blankTime;
	/** 中抜け時間（画面表示用） */
	private String blankTimeValue;
	/** 出勤・時間マップ（プルダウン） */
//	private LinkedHashMap<Integer, String> trainingTimeStartHours;
	private Integer trainingTimeStartHours;
	/** 出勤・時間マップ（表示用） */
	private String trainingTimeStartHoursValue;
	/** 退勤・時間マップ（プルダウン） */
//	private LinkedHashMap<Integer, String> trainingTimeEndHours;
	private Integer trainingTimeEndHours;
	//** 退勤・時間マップ（表示用） */
	private String trainingTimeEndHoursValue;	
	/** 出勤・分マップ（プルダウン） */
//	private LinkedHashMap<Integer, String> trainingTimeStartMinutes;
	private Integer trainingTimeStartMinutes;
	/** 出勤・分マップ（表示用） */
	private String trainingTimeStartMinutesValue;
	/** 退勤・分マップ（プルダウン） */
//	private LinkedHashMap<Integer, String> trainingTimeEndMinutes;
	private Integer trainingTimeEndMinutes;
	/** 退勤・分マップ（表示用） */
	private String trainingTimeEndMinutesValue;
	/** ステータス */
	private String status;
	/** 備考 */
	private String note;
	/** セクション名 */
	private String sectionName;
	/** 当日フラグ */
	private Boolean isToday;
	/** エラーフラグ */
	private Boolean isError;
	/** 日付（画面表示用） */
	private String dispTrainingDate;
	/** ステータス（画面表示用） */
	private String statusDispName;
	/** LMSユーザーID */
	private String lmsUserId;
	/** ユーザー名 */
	private String userName;
	/** コース名 */
	private String courseName;
	/** インデックス */
	private String index;

}
