package de.checkpoint.server.appServer;

public class AppResourceBundle_en extends AppResourceBundle
{
	static final Object[][] contents = {
		//{APPLICATION_NAME, "CheckpointDRG"},
		{VERSION, "Version"},
		//{TITLE, "CheckpointDRG-Casemanagement"},
		//{COPYRIGHT, "(c) Lohmann & Birkner GmbH"},

		{BUTTON_LOGIN,  "Login"},
		{BUTTON_LOGIN_MNEMONIC, "L"},
		{BUTTON_SELECT_ALL, "all"},
		{BUTTON_DESELECT_ALL, "none"},
		{BUTTON_SAVE_CONF, "save"},


		{FILEMENU_FILE,  "File"},
		{FILEMENU_EXIT,  "Exit"},
		{FILEMENU_FILE_MNEMONIC, "F"},
		{FILEMENU_EXIT_MNEMONIC, "x"},
		{FILEMENU_LOGOUT,  "Logout"},
		{FILEMENU_LOGOUT_MNEMONIC, "t"},
		{FILEMENU_LOGIN,  "Login"},
		{FILEMENU_LOGIN_MNEMONIC, "n"},

		{MENU_FOLDER, "Ordner"},
		{MENU_NEW, "New"},
		{MENU_NEW_FROM, "new from version"},
		{MENU_RENAME, "Rename"},
		{MENU_DELETE, "Delete"},
		{MENU_DELETE_FROM, "delete version"},
		{MENU_SHARING, "Sharing"},
		{MENU_COPY, "copy"},
		{MENU_PASTE, "paste"},
		{MENU_PAGE_FORWARD, "page forward"},
		{MENU_PAGE_BACK, "page back"},
		{MENU_SAVE, "save"},
		{MENU_SHOW, "show"},
		{MENU_OPEN, "open"},
		{MENU_CLOSE, "close"},
		{MENU_DIAGNOSE_AS_MAIN, "set as main diagnose"},
		{MENU_VERSION, "version manager"},
		{MENU_VERSION_MNEMONIC, "v"},
		{MENU_SHOW_LOCAL, "show local"},
		{MENU_SHOW_EXTERN, "show external"},
		{MENU_EX_PRINT_TABLE, "exportprewiev"},
		{MENU_EX_CONF_TABLE, "choose columns for export "},
		{MENU_EX_PDF_TABLE, "export table as PDF"},
		{MENU_EX_XLS_TABLE, "export table as XLS"},
		{MENU_EX_HTML_TABLE, "export table as  HTML"},
		{MENU_EX_CSV_TABLE, "export table as  TXT(CSV)"},

		{MENU_APP, "Program"},
		{MENU_APP_MNEMONIC, "P"},
		{MENU_APP_EXIT, "exit"},
		{MENU_APP_EXIT_MNEMONIC, "e"},
		{MENU_APP_LOGOUT, "logout"},
		{MENU_APP_LOGOUT_MNEMONIC, "l"},

		{TXT_LOGIN, "Login"},
		{TXT_USER, "User"},
		{TXT_PASSWORD, "Password"},
		{TXT_ORGANISATION_MODEL, "model of the organisation"},
		{TXT_WORKING_LIST, "working list"},
		{TXT_WORKSTATION, "workstation"},
		{TXT_ROLE, "Role"},
		{TXT_VALID_FROM, "val   id from "},
		{TXT_VALID_TO, "valid to "},
		{TXT_PATIENT_DATES, "patient dates"},
		{TXT_CASE_DATES, "case dates"},
		{TXT_IDENTIFICATION, "Identification"},
		{TXT_GENDER, "gender"},
		{TXT_AGE, "age"},
		{TXT_ADMISSION_WEIGHT, "admission weight"},
		{TXT_BIRTHDATE, "birth date"},
		{TXT_AGE_ODER_14, "age >= 14 years"},
		{TXT_CASELIST_OF_SPECIALTIY, "caselist "},
		{TXT_DIAGNOSIS, "diagnosis"},
		{TXT_DIAGNOSIS_LOCAL, "diagnosis local"},
		{TXT_DIAGNOSIS_EXTERN, "diagnosis external"},
		{TXT_DIAGNOSIS_HD, "MD"},
		{TXT_PROCEDURES, "procedures"},
		{TXT_PROCEDURES_LOCAL, "procedures local"},
		{TXT_PROCEDURES_EXTERN, "procedures external"},

		{DRG, "DRG"},
		{DRG_CCL, "CCL"},
		{DRG_INTERN, "local DRG"},
		{DRG_EXTERN, "external DRG"},
		{DRG_COMPUTE, "compute DRG"},
		{DRG_GROUPED, "G"},
		{DRG_CW, "CW"},
		{DRG_CW_UNK, "unk. CW"},

		{DATE_YEARS, "years"},
		{DATE_MONTHS, "Months"},
		{DATE_PREV_YEAR, "previous year"},
		{DATE_PREV_MONTH, "previous month"},
		{DATE_NEXT_YEAR, "next year"},
		{DATE_NEXT_MONTH, "next Month"},
		{DATE_DAY_1_SHORT, "Sun"},
		{DATE_DAY_2_SHORT, "Mon"},
		{DATE_DAY_3_SHORT, "Tue"},
		{DATE_DAY_4_SHORT, "Wed"},
		{DATE_DAY_5_SHORT, "Thu"},
		{DATE_DAY_6_SHORT, "Fri"},
		{DATE_DAY_7_SHORT, "Sat"},

		{CASE,  "case"},
		{CASE_INTERN, "internal case"},
		{CASE_EXTERN, "external case"},
		{CASE_ENCOUNTER, "encounter"},
		{CASE_FINAL_EXAMINATION, "Fallabschluss"},
		{CASE_EXTERNAL_VERSION, "external version"},
		{CASE_INTERNAL_VERSION, "local version"},


		{COL_CASENO, "case No"},
		{COL_TITLE, "Title"},
		{COL_FIRSTNAME, "first name"},
		{COL_LASTNAME, "last name"},
		{COL_STATE, "state"},
		{COL_ENTLFA, "discharge sp"},
		{COL_CREATION_DATE, "creation date"},
		{COL_ENTL_DATE, "discharge date"},
		{COL_MEMO, "memo"},
		{COL_FACHABT, "Facility"},
		{COL_FACHABT_301, "Facility §301"},
		{COL_FACHABT_TEXT, "Text"},
		{COL_BASERATE, "Baserate"},
		{COL_PROCEEDS, "Proceeds"},
		{COL_CASESUM, "Casesum"},
		{COL_KNZ_MDC_TEXT, "MDC Text"},
		{COL_KNZ_MDC, "MDC"},

		{COL_TTB_NAME, "Name"},
		{COL_TTB_VALUE, "Value"},


		{TAB_ORGANISATION, "organisation"},
		{TAB_WORKSTATION, "work station"},
		{TAB_PATIENT, "    Case    "},

		{MSG_DEL_OBJECT_1, "Do you want to delete the entry  : \n"},
		{MSG_DEL_OBJECT_2, ""},

		{FRAME_DIALOG_EX_CONF_TABLE,"Columnchooser for tableexport"}

	};

	public Object[][] getContents() {
		return contents;
	}
}
