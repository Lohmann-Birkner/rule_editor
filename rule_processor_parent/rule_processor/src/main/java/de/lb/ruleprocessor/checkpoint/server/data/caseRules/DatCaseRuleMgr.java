package de.lb.ruleprocessor.checkpoint.server.data.caseRules;

import java.util.*;

import org.w3c.dom.*;


import de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations;




public class DatCaseRuleMgr extends DatCaseRuleConstants
{
// name of crit
	public static final String CRIT_INSTITUTE = "Institutionskennzeichen"; // fall
	public static final String CRIT_ADMISSION_DOC_DIS = "Einweisender Arzt"; // fall
	public static final String CRIT_ADMISSION_DOC = "EinweisenderArzt"; // fall
	public static final String CRIT_ADMISSION_HOSPITAL_DIS = "Einweisendes Krankenhaus"; // fall
	public static final String CRIT_ADMISSION_HOSPITAL = "EinweisendesKrankenhaus"; // fall
	public static final String CRIT_HEALTH_ENSURANCE = "Krankenkasse"; // fall
	public static final String CRIT_BREATHING = "Beatmungsdauer"; // fall
	public static final String CRIT_WEIGHT = "Gewicht"; // fall
	public static final String CRIT_VWD = "Verweildauer"; // fall
	public static final String CRIT_VWD_IN_HOURS = "Verweildauer In Stunden"; // fall
	public static final String CRIT_LOSLESS24h = "Verweildauer kleiner 24h"; //fall
	public static final String CRIT_ADMISSION_DATE = "Aufnahmedatum"; //fall
	public static final String CRIT_ADMISSION_JEAR = "Aufnahmejahr"; // fall
	public static final String CRIT_ADMISSION_MONTH = "Aufnahmemonat"; //fall
	public static final String CRIT_ADMISSION_DAY = "Aufnahmetag"; //fall
	public static final String CRIT_ADMISSION_DAY_TIME = "Aufnahmeuhrzeit"; //fall
	public static final String CRIT_ADMISSION_DAY_TIME_DIS = "Aufnahmeuhrzeit"; //fall
	public static final String CRIT_ADMISSION_WEEK_DAY = "Aufnahmewochentag"; //f
	public static final String CRIT_SEPARATION_DATE = "Entlassungsdatum"; // f
	public static final String CRIT_SEPARATION_YEAR = "Entlassungsjahr"; // f
	public static final String CRIT_SEPARATION_MONTH = "Entlassungsmonat"; //f
	public static final String CRIT_SEPARATION_DAY = "Entlassungstag"; // f
	public static final String CRIT_SEPARATION_DAY_TIME = "Entlassungsuhrzeit"; // f
	public static final String CRIT_SEPARATION_WEEK_DAY = "Entlassungswochentag"; //f
	public static final String CRIT_ADM_CAUSE = "Aufnahmeanlass"; //fall
	public static final String CRIT_ADM_TYPE = "Aufnahmegrund1"; //f
	public static final String CRIT_ADM2_TYPE = "Aufnahmegrund2"; //f
	public static final String CRIT_DIS_TYPE = "Entlassungsgrund12"; //f
	public static final String CRIT_DIS_TYPE3 = "Entlassungsgrund3"; //f
	public static final String CRIT_INTENSIV_STAY = "Intensivverweildauer"; //f
	public static final String CRIT_TRANSFER_DATE = "Verlegungsdatum"; //fall
	public static final String CRIT_TRANSFER_DAY_TIME = "Verlegungsuhrzeit"; //fall
	public static final String CRIT_CARE_STATE = "Pflegestatus"; //f
	public static final String CRIT_PSYCHO_STATE = "Psychostatus"; //f
	public static final String CRIT_VOLUNTARY_DAYS_DIS = "Tage ohne Berechnung"; //f
	public static final String CRIT_VOLUNTARY_DAYS = "TageOhneBerechnung";
	public static final String CRIT_ACCOUCHEMENT_DATE_1 = "TagDerEntbindung1";
	public static final String CRIT_ACCOUCHEMENT_DATE_2 = "TagDerEntbindung2";
	public static final String CRIT_ACCOUCHEMENT_DATE_1_DIS = "Tag der Entbindung1";
	public static final String CRIT_ACCOUCHEMENT_DATE_2_DIS = "Tag der Entbindung2";
//tooltips
	public static final String CRIT_INSTITUTE_TOOLTIP = "Institutskennzeichen des Krankenhauses"; // fall
	public static final String CRIT_ADMISSION_DOC_TOOLTIP = "Einweisender Arzt"; // fall
	public static final String CRIT_ADMISSION_HOSPITAL_TOOLTIP = "Einweisendes Krankenhaus"; // fall
	public static final String CRIT_HEALTH_ENSURANCE_TOOLTIP = "Kennzeichen der Krankenkasse"; // fall
	public static final String CRIT_BREATHING_TOOLTIP = "Beatmungsdauer in Stunden"; // fall
	public static final String CRIT_WEIGHT_TOOLTIP = "Gewicht in g"; // fall
	public static final String CRIT_VWD_TOOLTIP = "Krankenhausaufenthaltsdauer in Tagen"; // fall
	public static final String CRIT_VWD_IN_HOURS_TOOLTIP = "Krankenhausaufenthaltsdauer in Stunden"; // fall
	public static final String CRIT_LOSLESS24h_TOOLTIP = "Krankenhausaufenthaltsdauer kleiner als 24 Stunden"; //fall
	public static final String CRIT_ADMISSION_DATE_TOOLTIP = "Aufnahmedatum des Patienten (jjjjmmtt)"; //fall
	public static final String CRIT_TRANSFER_DATE_TOOLTIP = "Verlegungsdatum des Patienten (jjjjmmtt)"; //fall
	public static final String CRIT_TRANSFER_DAY_TIME_TOOLTIP = "Verlegungsuhrzeit des Patienten (hh:mm / hh:mm:ss)"; //fall
	public static final String CRIT_ADMISSION_JEAR_TOOLTIP = "Aufnahmejahr des Patienten (jjjj)"; // fall
	public static final String CRIT_ADMISSION_MONTH_TOOLTIP = "Aufnahmemonat des Patienten (1 - 12)"; //fall
	public static final String CRIT_ADMISSION_DAY_TOOLTIP = "Aufnahmetag des Patienten (1 - 31 )"; //fall
	public static final String CRIT_ADMISSION_DAY_TIME_TOOLTIP = "Aufnahmeuhrzeit des Patienten (hh:mm / hh:mm:ss)"; //fall
	public static final String CRIT_ADMISSION_WEEK_DAY_TOOLTIP = "Aufnahmewochentag des Patienten (Mo, Di, Mi, Do, Fr, Sa, So)"; //f
	public static final String CRIT_SEPARATION_DATE_TOOLTIP = "Entlassungsdatum des Patienten (jjjjmmtt)"; // f
	public static final String CRIT_SEPARATION_YEAR_TOOLTIP = "Entlassungsjahr des Patienten (jjjj)"; // f
	public static final String CRIT_SEPARATION_MONTH_TOOLTIP = "Entlassungsmonat des Patienten (1 - 12)"; //f
	public static final String CRIT_SEPARATION_DAY_TOOLTIP = "Entlassungstag des Patienten (1 - 31)"; // f
	public static final String CRIT_SEPARATION_DAY_TIME_TOOLTIP = "Entlassungsuhrzeit des Patienten (hh:mm / hh:mm:ss)"; // f
	public static final String CRIT_SEPARATION_WEEK_DAY_TOOLTIP = "Entlassungswochentag des Patienten (Mo, Di, Mi, Do, Fr, Sa, So)"; //f
	public static final String[] CRIT_ADM_CAUSE_TOOLTIP = {"Aufnahmeanlass: ",
		 "E - Einweisung durch Arzt ",
		 "Z - Einweisung durch Zahnarzt ",
		 "N - Notfall ",
		 "R - Aufnahme nach vorausgehender Behandlung in einer Rehaeinrichtung ",
		 "V - Verlegung ",
		 "K - Verlegung (Aufnahme) aus einem anderen Krankenhaus i.R.e. Kooperation ",
		 "G - Geburt ",
		 "B - Begleitperson ",
		 "A - Verlegung (mit Behandlungsdauer im verlegenden Krankenhaus bis zu 24 Stunden)"}; //fall
	public static final String CRIT_CARE_STATE_TOOLTIP = "Pflegestatus: <br>  (0-kein, ansonsten 1-5)"; //f
	public static final String CRIT_PSYCHO_STATE_TOOLTIP = "Psychstatus: <br>" +
		"1 - freiwillig <br>2 - unfreiwillig"; //f
	public static final String CRIT_VOLUNTARY_DAYS_TOOLTIP = "Tage ohne Berechnung"; //f
	public static final String CRIT_ACCOUCHEMENT_DATE_1_TOOLTIP = "Tag der Entbindung1";
	public static final String CRIT_ACCOUCHEMENT_DATE_2_TOOLTIP = "Tag der Entbindung2";

	//interval
	public static String INTERVAL ="Zeitinterval:";
	public static final String [] CRIT_ADM1_TYPE_TOOLTIP =
		   { "01 - Krankenhausbehandlung, vollstationär",
		     "02 - Krankenhausbehandlung, vollstationär mit voraus. vorstat. Behandlung",
		     "03 - Krankenhausbehandlung teilstationär",
		     "04 - Krankenhausbehandlung ohne anschl. stationäre Behandlung",
		     "05 - stationäre Entbindung",
		     "06 - Geburt",
		     "07 - Wiederaufnahme wg. Komplikation (Fallpauschale)",
	  "08 - Stationäre Aufnahme zur Organentnahme"};

	  public static final String [] CRIT_ADM2_TYPE_TOOLTIP = {
		   "01 - Normalfall",
		   "02 - Arbeitsunfall / Wegeunfall / Berufskrankheit (§ 11 Abs. 4 SGB V)",
		   "03 - Verkehrsunfall / Sportunfall / Sonstiger Unfall (z.B. § 116 SGB X)",
		   "04 - Hinweis auf Einwirkung von äußerer Gewalt",
		   "06 - Kriegsbeschädigten-Leiden / BVG-Leiden",
		   "07 - Notfall",
		   "21 - Kostenträgerwechsel - Normalfall",
		   "22 - Kostenträgerwechsel - Arbeitsunfall / Wegeunfall / Berufskrankheit (§ 11 Abs. 4 SGB V)",
		   "23 - Kostenträgerwechsel - Verkehrsunfall / Sportunfall / Sonstiger Unfall (z.B. § 116 SGB X)",
		   "24 - Kostenträgerwechsel - Hinweis auf Einwirkung von äußerer Gewalt",
		   "26 - Kostenträgerwechsel - Kriegsbeschädigten-Leiden / BVG-Leiden",
		   "27 - Kostenträgerwechsel - Notfall"}; //f

	public static final String [] CRIT_DIS_TYPE_TOOLTIP = {
		"01 - Behandlung regulär beendet",
		"02 - Behandlung regulär beendet, nachstationäre Behandlung vorgesehen",
		"03 - Behandlung aus sonstigen Gründen beendet",
		"04 - Behandlung gegen ärztlichen Rat beendet",
		"05 - Zuständigkeitswechsel des Kostenträger",
		"06 - Verlegung in ein anderes Krankenhaus",
		"07 - Tod",
		"08 - Verlegung in ein anderes Krankenhaus i.R.e. Kooperation",
		"09 - Entlassung in eine Rehabilitationseinrichtung",
		"10 - Entlassung in eine Pflegeeinrichtung",
		"11 - Entlassung in ein Hospiz",
		"12 - Interne Verlegung",
		"13 - Externe Verlegung zur psychiatrischen Behandlung",
		"14 - Behandlung aus sonstigen Gründen beendet, nachstationäre Beh.",
		"15 - Behandlung gegen ärztlichen Rat beendet, nachstationäre Beh.",
		"17 - interne Verlegung mit Wechsel zwischen den Geltungsbereichen",
		"18 - Rückverlegung",
		"19 - Entlassung vor Wiederaufnahme mit Neueinstufung",
		"20 - Entlassung vor Wiederaufnahme mit Neueinstufung wegen Komplikation",
		"21 - Entlassung mit nachfolgender Wiederaufnahme",
		"22 - Fallabschluss (interne Verlegung) bei Wechsel zwischen voll- und teilstat. Behandlung"}; //f
	public static final String [] CRIT_DIS_TYPE3_TOOLTIP = {
		"1 - arbeitsfähig", "2 - nicht arbeitsfähig", "9 - keine Angabe"}; //f

	public static final String CRIT_INTENSIV_STAY_TOOLTIP = "Intensivverweildauer"; //f
//tooltips entgelt

	public static final String CRIT_BASE_VALUE_TOOLTIP = "Basisfallwert (Baserate)"; // entgelt
	public static final String CRIT_DRG_TOOLTIP = "DRG"; //entgelt
	public static final String CRIT_DRGPARTITION_TOOLTIP = "DRG Partition"; //entgelt
	public static final String CRIT_ADRG_TOOLTIP = "Basis-DRG – DRG ohne Schweregrad"; //eg
	public static final String CRIT_MDC_TOOLTIP = "MDC"; // eg
	public static final String CRIT_TOP_LENGTH_OF_STAY_TOOLTIP = "Obere Grenzverweildauer einer DRG"; // eg
	public static final String CRIT_MID_LENGTH_OF_STAY_TOOLTIP = "Mittlere Grenzverweildauer einer DRG"; //eg
	public static final String CRIT_BOTTOM_LENGTH_OF_STAY_TOOLTIP = "Untere Grenzverweildauer einer DRG"; //eg;
	public static final String CRIT_REDUCTION_DAYS_TOOLTIP = "Anzahl der Abschlagstage"; //eg
	public static final String CRIT_BONUS_DAYS_TOOLTIP = "Anzahl der Zuschlagstage"; //eg
	public static final String CRIT_CW_CATALOG_TOOLTIP = "Kostengewicht / Kostenrelation aus dem DRG-Katalog"; //eg
	public static final String CRIT_CW_EFFECTIV_TOOLTIP = "Effektives Kostengewicht: berücksichtigt die im Fall entstandenen Zu- und Abschläge"; //eg
	public static final String CRIT_REDUCTION_CW_TOOLTIP = "Kostengewicht des Abschlags"; //eg
	public static final String CRIT_BONUS_CW_TOOLTIP = "Kostengewicht des Zuschlags"; //eg
	public static final String CRIT_FEE_NUMBER_TOOLTIP = "Anzahl der Entgelte"; //eg
//	public static final String CRIT_FEE_TOOLTIP = "Entgelte"; //eg
	public static final String CRIT_FEE_SUM_TOOLTIP = "Summe der Entgelte"; //eg
	public static final String CRIT_DIAGNOSE_GROUPED_TOOLTIP = "Grouperrelevante Diagnose"; //eg
	public static final String CRIT_PROCEDURE_GROUPED_TOOLTIP = "Grouperrelevantes Prozedur"; //eg
	public static final String CRIT_ENTGELTART_TOOLTIP = "Entgeltschluessel"; //eg
	public static final String CRIT_ENTGELTEINZELBETRAG_TOOLTIP = "Entgelteinzelbetrag"; //eg
	public static final String CRIT_ENTGELTABRECHNUNG_VON_TOOLTIP = "Entgeltabrechnung von"; //eg
	public static final String CRIT_ENTGELTABRECHNUNG_BIS_TOOLTIP = "Entgeltabrechnung bis"; //eg
	public static final String CRIT_ENTGELTANZAHL_TOOLTIP = "Entgeltanzahl je Entgelt"; //eg
	public static final String CRIT_TAGE_OHNE_BERECHNUNG_PRO_ENTGELT_TOOLTIP = "Tage ohne Berechnung pro Entgelt";
	public static final String CRIT_ENTGELTSUMMEJEENTGELT_TOOLTIP = "Entgeltsumme je Entgelt"; //eg
//Rechnung
	public static final String CRIT_RECHNUNG_NUMMER_TOOLTIP = "Rechnungsnummer"; //eg
	public static final String CRIT_RECHNUNG_DATUM_TOOLTIP = "Rechnungsdatum"; //eg
	public static final String CRIT_RECHNUNG_ART_TOOLTIP = "Rechnungsart"; //eg
// entgelt
	public static final String CRIT_BASE_VALUE = "Basisfallwert"; // entgelt
	public static final String CRIT_DRG = "DRG"; //entgelt
	public static final String CRIT_DRGPARTITION = "DRGPartition"; //entgelt
	public static final String CRIT_ADRG = "ADRG"; //eg
	public static final String CRIT_MDC = "MDC"; // eg
	public static final String CRIT_TOP_LENGTH_OF_STAY = "obere Grenzverweildauer"; // eg
	public static final String CRIT_MID_LENGTH_OF_STAY = "mittlere Verweildauer"; //eg
	public static final String CRIT_BOTTOM_LENGTH_OF_STAY = "untere Grenzverweildauer"; //eg;
	public static final String CRIT_REDUCTION_DAYS = "Abschlagstage"; //eg
	public static final String CRIT_BONUS_DAYS = "Zuschlagstage"; //eg
	public static final String CRIT_CW_CATALOG = "Kostengewicht Katalog"; //eg
	public static final String CRIT_CW_EFFECTIV = "Kostengewicht effektive"; //eg
	public static final String CRIT_REDUCTION_CW = "Abschlags_CW"; //eg
	public static final String CRIT_BONUS_CW = "Zuschlags_CW"; //eg
	public static final String CRIT_FEE_NUMBER = "Anzahl Entgelte"; //eg
//	public static final String CRIT_FEE = "Entgeltschlüssel"; //eg
	public static final String CRIT_FEE_SUM = "Summe der Entgelte"; //eg
	public static final String CRIT_DIAGNOSE_GROUPED = "GrouperrelevanteDiagnose"; //eg
	public static final String CRIT_PROCEDURE_GROUPED = "GrouperrelevanteProzedur"; //eg
	public static final String CRIT_DIAGNOSE_GROUPED_DIS = "Grouperrelevante Diagnose"; //eg
	public static final String CRIT_PROCEDURE_GROUPED_DIS = "Grouperrelevantes Prozedur"; //eg
//Entgeltliste
	public static final String CRIT_ENTGELTART = "Entgelt"; //eg
	public static final String CRIT_ENTGELTEINZELBETRAG = "Entgelteinzelbetrag"; //eg
	public static final String CRIT_ENTGELTABRECHNUNG_VON = "EntgeltabrechnungVon"; //eg
	public static final String CRIT_ENTGELTABRECHNUNG_VON_DIS = "Entgeltabrechnung von"; //eg
	public static final String CRIT_ENTGELTABRECHNUNG_BIS = "EntgeltabrechnungBis"; //eg
	public static final String CRIT_ENTGELTABRECHNUNG_BIS_DIS = "Entgeltabrechnung bis"; //eg
	public static final String CRIT_ENTGELTANZAHL = "EntgeltanzahlJeEntgelt"; //eg
	public static final String CRIT_ENTGELTANZAHL_DIS = "Entgeltanzahl je Entgelt"; //eg
	public static final String CRIT_ENTGELTSUMMEJEENTGELT_DIS = "Entgeltsumme je Entgelt";
	public static final String CRIT_ENTGELTSUMMEJEENTGELT = "EntgeltsummeJeEntgelt";
	public static final String CRIT_TAGE_OHNE_BERECHNUNG_PRO_ENTGELT_DIS = "Tage ohne Berechnung je Entgelt";
	public static final String CRIT_TAGE_OHNE_BERECHNUNG_PRO_ENTGELT = "TageOhneBerechnungProEntgelt";
//Rechnung
	public static final String CRIT_RECHNUNG_NUMMER = "Rechnungsnummer"; //eg
	public static final String CRIT_RECHNUNG_DATUM = "Rechnungsdatum"; //eg
	public static final String CRIT_RECHNUNG_ART = "Rechnungsart"; //eg
// patient
	public static final String CRIT_AGE_YEARS = "AlterInJahren"; //pat
	public static final String CRIT_AGE_MONTHS = "AlterInTagen"; // pat
	public static final String CRIT_AGE_YEARS_DIS = "Alter in Jahren"; //pat
	public static final String CRIT_AGE_MONTHS_DIS = "Alter in Tagen"; //pat
	public static final String CRIT_GENDER = "Geschlecht"; // pat
	public static final String CRIT_BIRTHDAY = "Geburtsdatum"; //pat ??
	public static final String CRIT_BIRTHDAY_DIS = "Geburtsdatum"; //pat  ??
	public static final String CRIT_INSURANCE_STATUS = "Versichertenstatus";
	public static final String CRIT_ZIP_CODE = "Postleitzahl";
	public static final String CRIT_CITY = "Wohnort";
//tooltips
	public static final String CRIT_AGE_YEARS_TOOLTIP = "Alter des Patienten in Jahren"; //pat
	public static final String CRIT_AGE_MONTHS_TOOLTIP = "Alter des Patienten in Tagen"; // pat
	public static final String CRIT_GENDER_TOOLTIP = "Geschlecht des Patienten"; // pat
	public static final String CRIT_INSURANCE_STATUS_TOOLTIP = "Versichertenstatus des Patienten:<ul><li>1 - selbstversichert</li><li>3 - famlilenversichert</li><li>5 - Rentner und Angehörige</li></ul>";
	public static final String CRIT_ZIP_CODE_TOOLTIP = "Postleitzahl";
	public static final String CRIT_CITY_TOOLTIP = "Wohnort";
	public static final String CRIT_BIRTHDAY_TOOLTIP = "Geburtsdatum des Patienten";

//kodierung
	public static final String CRIT_PRINC_DIAG = "Hauptdiagnose"; // kodierkriterien
	public static final String CRIT_PRINC_DIAG_LOC = "HauptdiagnoseLokalisation"; // kodierkriterien
	public static final String CRIT_PRINC_DIAG_LOC_DIS = "Hauptdiagnose: Lokalisation"; // kodierkriterien
	public static final String CRIT_ADMISSION_DIAG = "Aufnahmediagnose"; // kodierkriterien
	public static final String CRIT_AUX_DIAG = "Nebendiagnose"; // KR
	public static final String CRIT_AUX_DIAG_LOC_DIS = "Nebendiagnose: Lokalisation"; // KR
	public static final String CRIT_AUX_DIAG_TYPE_DIS = "Nebendiagnose: Typ"; // KR
	public static final String CRIT_AUX_DIAG_LOC = "NebendiagnoseLokalisation"; // KR
	public static final String CRIT_AUX_DIAG_TYPE = "NebendiagnoseType"; // KR
	public static final String CRIT_DIAG = "Diagnose"; //kr
	public static final String CRIT_DIAG_LOC_DIS = "Diagnose: Lokalisation"; //kr
	public static final String CRIT_DIAG_TYPE_DIS = "Diagnose: Typ"; //kr
	public static final String CRIT_DIAG_LOC = "DiagnoseLokalisation"; //kr
	public static final String CRIT_DIAG_TYPE = "DiagnoseType"; //kr
	public static final String CRIT_SEC_DIAG_DIS = "Sek. Diagnose"; //kr
	public static final String CRIT_SEC_DIAG_LOC_DIS = "Sek. Diagnose: Lokalisation"; //kr
	public static final String CRIT_SEC_DIAG_TYPE_DIS = "Sek. Diagnose: Type"; //kr
	public static final String CRIT_SEC_DIAG_PRIM_DIS = "Sek. Diagnose: Primär"; //kr
	public static final String CRIT_SEC_DIAG = "SekDiagnose"; //kr
	public static final String CRIT_SEC_DIAG_LOC = "SekDiagnoseLokalisation"; //kr
	public static final String CRIT_SEC_DIAG_TYPE = "SekDiagnoseType"; //kr
	public static final String CRIT_SEC_DIAG_PRIM = "SekDiagnosePrim"; //kr
	public static final String CRIT_PROC = "Prozedur"; //kr
	public static final String CRIT_PROC_DATE = "OPSDatum"; //kr
	public static final String CRIT_PROC_LOCALISATION = "OPSLokalisation"; //kr
	public static final String CRIT_PCCL = "PCCL"; //eg
	public static final String CRIT_PROC_NUMBER = "Anzahl Prozeduren"; //kr
	public static final String CRIT_SDX_NUMBER = "Anzahl Nebendiagnosen"; //kr
	public static final String CRIT_EQUAL_PROC_NUMBER = "Anzahl gleicher Prozeduren"; //k

	public static final String CRIT_PRINC_DIAG_TOOLTIP = "Hauptdiagnose einer Kodierung"; // kodierkriterien
	public static final String CRIT_PRINC_DIAG_LOC_TOOLTIP = "Lokalisation der Hauptdiagnose einer Kodierung, nur in Verbindung mit einem Prozedurenkode sinnvoll"; // kodierkriterien
	public static final String CRIT_ADMISSION_DIAG_TOOLTIP = "Aufnahmediagnose einer Kodierung"; // kodierkriterien
	public static final String CRIT_AUX_DIAG_TOOLTIP = "Nebendiagnose einer Kodierung"; // KR


	public static final String CRIT_AUX_DIAG_LOC_TOOLTIP = "Lokalisation einer Nebendiagnose, nur in Verbindung mit einem Nebendiagnosenkode sinnvoll"; // KR
	public static final String CRIT_AUX_DIAG_TYPE_TOOLTIP = "Typ einer Nebendiagnose, nur in Verbindung mit einem Nebendiagnosenkode sinnvoll"; // KR
	public static final String CRIT_DIAG_LOC_TOOLTIP = "Lokalisation einer Diagnose, nur in Verbindung mit einem Diagnosenkode sinnvoll"; //kr
	public static final String CRIT_DIAG_TYPE_TOOLTIP = "Type einer Diagnose, nur in Verbindung mit einem Diagnosenkode sinnvoll"; //kr
	public static final String CRIT_SEC_DIAG_TOOLTIP = "Sekundäre Diagnose"; //kr
	public static final String CRIT_SEC_DIAG_LOC_TOOLTIP = "Lokalisation einer sekundären Diagnose, nur in Verbindung mit einem Diagnosenkode sinnvoll"; //kr
	public static final String CRIT_SEC_DIAG_TYPE_TOOLTIP = "Type einer sekundären Diagnose, nur in Verbindung mit einem Diagnosenkode sinnvoll"; //kr
	public static final String CRIT_SEC_DIAG_PRIM_TOOLTIP = "Primär einer sekundären Diagnose, nur in Verbindung mit einem Diagnosenkode sinnvoll"; //kr

	public static final String CRIT_DIAG_TOOLTIP = "Diagnose einer Kodierung"; //kr
	public static final String CRIT_PROC_TOOLTIP = "Am Fall erbrachte Operationen und Prozeduren"; //kr
	public static final String CRIT_PROC_DATE_TOOLTIP = "Datum der Operation/Prozedur, nur in Verbindung mit einem Prozedurenkode sinnvoll"; //kr
	public static final String CRIT_PROC_LOCALISATION_TOOLTIP = "Lokalisation der Operation/Prozedur, nur in Verbindung mit einem Prozedurenkode sinnvoll" +
		"<ul><li>R oder 1 für rechts</li><li>L oder 2 für links</li><li>B oder 3 für beideseitig</li></ul>"; //kr
	public static final String CRIT_PCCL_TOOLTIP = "Schweregrad des Falles"; //eg
	public static final String CRIT_PROC_NUMBER_TOOLTIP = "Anzahl Prozeduren"; //kr
	public static final String CRIT_SDX_NUMBER_TOOLTIP = "Anzahl Nebendiagnosen"; //kr
	public static final String CRIT_EQUAL_PROC_NUMBER_TOOLTIP = "Anzahl der übereinstimmenden Prozeduren und Operationen"; //k

// bewegungen
	public static final String CRIT_DEPARTMENT = "Fachabteilungsbewegung"; //bewegungen ?????
	public static final String CRIT_MOVEMENTS_COUNT = "Anzahl Bewegungen"; //bw
	public static final String CRIT_STATION = "Station"; //b
	public static final String CRIT_RELEASE_DEPARTMENT = "EntlassendeFachabteilung"; //b
	public static final String CRIT_RELEASE_DEPARTMENT_DIS = "entlassende Fachabteilung"; //b
	public static final String CRIT_COMBAT_DEPARTMENT = "BehandelndeFachabteilung"; //b
	public static final String CRIT_COMBAT_DEPARTMENT_DIS = "behandelnde Fachabteilung"; //bw
	public static final String CRIT_RECEIPT_DEPARTMENT = "AufnehmendeFachabteilung"; //bw
	public static final String CRIT_RECEIPT_DEPARTMENT_DIS = "aufnehmende Fachabteilung"; //bw
//tooltips
	public static final String CRIT_DEPARTMENT_TOOLTIP = "Bewegung des Patienten über die Fachabteilung"; //bewegungen
	public static final String CRIT_MOVEMENTS_COUNT_TOOLTIP = "Anzahl der Bewegungen"; //bw
	public static final String CRIT_STATION_TOOLTIP = "Station"; //b
	public static final String CRIT_RELEASE_DEPARTMENT_TOOLTIP = "entlassende Fachabteilung"; //b
	public static final String CRIT_COMBAT_DEPARTMENT_TOOLTIP = "behandelnde Fachabteilung"; //b
	public static final String CRIT_RECEIPT_DEPARTMENT_TOOLTIP = "aufnehmende Fachabteilung"; //bw

//kein krit
	public static final String CRIT_NO_CRIT = "kein Kriterium";
	public static final String CRIT_NO_CRIT_TOOLTIP = "kein Kriterium";
//sonstige
	public static final String CRIT_NOW_TIME = "Jetzt"; //so
	public static final String CRIT_SUGGESTION_FLAG = "Vorschlag"; //so
	public static final String CRIT_CASE_NUMERIC1 = "Fall.Numeric1"; //so
	public static final String CRIT_CASE_NUMERIC2 = "Fall.Numeric2"; //so
	public static final String CRIT_CASE_NUMERIC3 = "Fall.Numeric3";
	public static final String CRIT_CASE_NUMERIC4 = "Fall.Numeric4";
	public static final String CRIT_CASE_NUMERIC5 = "Fall.Numeric5";
	public static final String CRIT_CASE_STR1 = "Fall.String1";
	public static final String CRIT_CASE_STR2 = "Fall.String2";
	public static final String CRIT_CASE_STR3 = "Fall.String3";
	public static final String CRIT_CASE_STR4 = "Fall.String4";
	public static final String CRIT_CASE_STR5 = "Fall.String5";
//tooltips
	public static final String CRIT_NOW_TIME_TOOLTIP = "Zeitpunkt des Groupens"; //so
	public static final String CRIT_SUGGESTION_FLAG_TOOLTIP = "Vorschlag";
	public static final String CRIT_CASE_NUMERIC1_TOOLTIP = "Optional herstellbares numerisches Kriterium"; //so
	public static final String CRIT_CASE_NUMERIC2_TOOLTIP = "Optional herstellbares numerisches Kriterium"; //so
	public static final String CRIT_CASE_NUMERIC3_TOOLTIP = "Optional herstellbares numerisches Kriterium";
	public static final String CRIT_CASE_NUMERIC4_TOOLTIP = "Optional herstellbares numerisches Kriterium";
	public static final String CRIT_CASE_NUMERIC5_TOOLTIP = "Optional herstellbares numerisches Kriterium";
	public static final String CRIT_CASE_STR1_TOOLTIP = "Optional herstellbares alphanumerisches Kriterium";
	public static final String CRIT_CASE_STR2_TOOLTIP = "Optional herstellbares alphanumerisches Kriterium";
	public static final String CRIT_CASE_STR3_TOOLTIP = "Optional herstellbares alphanumerisches Kriterium";
	public static final String CRIT_CASE_STR4_TOOLTIP = "Optional herstellbares alphanumerisches Kriterium";
	public static final String CRIT_CASE_STR5_TOOLTIP = "Optional herstellbares alphanumerisches Kriterium";


// Medikamente
	public static final String CRIT_MEDICAMENT_ATC_CODE_DIS = "Medikament: ATC-Code";
	public static final String CRIT_MEDICAMENT_PZN_CODE_DIS = "Medikament: PZN-Code";
	public static final String CRIT_MEDICAMENT_PERSCRIPT_DATE_DIS = "Medikament: Verordnungsdatum";
	public static final String CRIT_MEDICAMENT_PRICE_DIS = "Medikament: Preis";
	public static final String CRIT_MEDICAMENT_ATC_CODE = "ATCCode";
	public static final String CRIT_MEDICAMENT_PZN_CODE = "PZNCode";
	public static final String CRIT_MEDICAMENT_PERSCRIPT_DATE = "MVerordnungsdatum";
	public static final String CRIT_MEDICAMENT_PRICE = "MPreis";
	public static final String CRIT_MEDICAMENT_PERSCRIPT_DOSAGE = "Dosierung";
	public static final String CRIT_MEDICAMENT_PERSCRIPT_DOSAGE_UNIT = "Dosierungseinheit";
	public static final String CRIT_MEDICAMENT_PERSCRIPT_DOSAGE_UNIT_DIS = "Medikament: Dosierungseinheit";
	public static final String CRIT_MEDICAMENT_PERSCRIPT_DOSAGE_DIS = "Medikament: Dosierung";
	public static final String CRIT_MEDICAMENT_FULL_PRICE_DIS = "Medikament: Gesamtpreis";
	public static final String CRIT_MEDICAMENT_FULL_PRICE_ATC_DIS = "Medikament: Gesamtpreis für ATC";
	public static final String CRIT_MEDICAMENT_FULL_PRICE_PZN_DIS = "Medikament: Gesamtpreis für PZN";
	public static final String CRIT_MEDICAMENT_FULL_PRICE_PZN = "GesamtPZNPreis";
	public static final String CRIT_MEDICAMENT_FULL_PRICE_ATC = "GesamtATCPreis";
	public static final String CRIT_MEDICAMENT_FULL_PRICE = "GesamtMPreis";
	public static final String CRIT_MEDICAMENT_FULL_DOSAGE_ATC_DIS = "Medikament: Gesamtdosis für ATC";
	public static final String CRIT_MEDICAMENT_FULL_DOSAGE_PZN_DIS = "Medikament: Gesamtdosis für PZN";
	public static final String CRIT_MEDICAMENT_FULL_DOSAGE_PZN = "GesamtPZNDosierung";
	public static final String CRIT_MEDICAMENT_FULL_DOSAGE_ATC = "GesamtATCDosierung";
//tooltips
	public static final String CRIT_MEDICAMENT_ATC_CODE_TOOLTIP = "Klassifikationscode für Arzneistoffe";
	public static final String CRIT_MEDICAMENT_PZN_CODE_TOOLTIP = "Pharmazentralnummer laut Arzneimittelpackung";
	public static final String CRIT_MEDICAMENT_PERSCRIPT_DATE_TOOLTIP = "Datum der Medikamentenverordnung";
	public static final String CRIT_MEDICAMENT_PRICE_TOOLTIP = "Preis des einzelnen Medikaments";
	public static final String CRIT_MEDICAMENT_PERSCRIPT_DOSAGE_TOOLTIP = "Dosierung";
	public static final String CRIT_MEDICAMENT_PERSCRIPT_DOSAGE_UNIT_TOOLTIP = "Dosierungseinheit";
	public static final String CRIT_MEDICAMENT_FULL_PRICE_PZN_TOOLTIP = "Preis der Arzneimittel mit einem bestimmten Pharmazentralnummer (PZN). Nur in Verbindung mit einem PZNCode sinnvoll";
	public static final String CRIT_MEDICAMENT_FULL_PRICE_ATC_TOOLTIP = "Preis der Arzneimittel mit einem bestimmten Klassifikationscode (ATC). Nur in Verbindung mit einem ATCCode sinnvoll";
	public static final String CRIT_MEDICAMENT_FULL_PRICE_TOOLTIP = "Gesamte Preis der Arzneimittel";
	public static final String CRIT_MEDICAMENT_FULL_DOSAGE_PZN_TOOLTIP = "Summe der Dosierungen einer über PZNCode bestimmter Arzneimittel. Nur in Verbindung mit einem PZNCode sinnvoll";
	public static final String CRIT_MEDICAMENT_FULL_DOSAGE_ATC_TOOLTIP = "Summe der Dosierungen einer über ATCCode bestimmter Arzneimittel. Nur in Verbindung mit einem ATCCode sinnvoll";


//SoLe
	public static final String CRIT_SOLE_HPN = "HPNCode";
	public static final String CRIT_SOLE_HPN_DIS = "SoLe: HPN";
	public static final String CRIT_SOLE_HIMINUMBER_DIS = "SoLe: Hilfsmittelnummer";
	public static final String CRIT_SOLE_HIMINUMBER = "HiMiNr";
	public static final String CRIT_SOLE_PRICE_DIS = "SoLe: Einzelpreis";
	public static final String CRIT_SOLE_PRICE = "SPreis";
	public static final String CRIT_SOLE_FULL_PRICE_DIS = "SoLe: Gesamtpreis";
	public static final String CRIT_SOLE_FULL_PRICE = "GesamtSPreis";
	public static final String CRIT_SOLE_PRESCRIPT_DATE = "SVerordnungsdatum";
	public static final String CRIT_SOLE_PRESCRIPT_DATE_DIS = "SoLe: Verordnungsdatum";
	public static final String CRIT_SOLE_NUMBER_DIS = "SoLe: Menge";
	public static final String CRIT_SOLE_NUMBER = "Menge";
	public static final String CRIT_SOLE_FULL_HPN_PRICE = "GesamtHPNPreis";
	public static final String CRIT_SOLE_FULL_HPN_PRICE_DIS = "SoLe: Gesamtpreis für HPN";
	public static final String CRIT_SOLE_FULL_PZN_PRICE = "GesamtHiMiNrPreis";
	public static final String CRIT_SOLE_FULL_PZN_PRICE_DIS = "SoLe: Gesamtpreis für Hilfsmittelnummer";
	public static final String CRIT_SOLE_FULL_HPN_NUMBER = "GesamtHPNMenge";
	public static final String CRIT_SOLE_FULL_HPN_NUMBER_DIS = "SoLe: Gesamtmenge für HPN";
	public static final String CRIT_SOLE_FULL_PZN_NUMBER = "GesamtHiMiMenge";
	public static final String CRIT_SOLE_FULL_PZN_NUMBER_DIS = "SoLe: Gesamtmenge für Hilfsmittelnummer";
//tooltips
	public static final String CRIT_SOLE_HPN_TOOLTIP = "Hilfsmittelpositionsnummer";
	public static final String CRIT_SOLE_HIMINUMBER_TOOLTIP = "Code des verschriebenen Hilfsmittels(HiMiNr)";
	public static final String CRIT_SOLE_PRICE_TOOLTIP = "Preis der einzelnen sonstigen Leistung";
	public static final String CRIT_SOLE_FULL_PRICE_TOOLTIP = "Preis aller sonstigen Leistungen";
	public static final String CRIT_SOLE_PRESCRIPT_DATE_TOOLTIP = "Datum der Verordnung einer sonstigen Leistung";
	public static final String CRIT_SOLE_NUMBER_TOOLTIP = "Anzahl der sonstigen Leistungen";
	public static final String CRIT_SOLE_FULL_HPN_PRICE_TOOLTIP = "Summe der Preise für eine über HPNCode bestimmten sonstige Leistung. Nur in Verbindung mit einem HPNCode sinnvo";
	public static final String CRIT_SOLE_FULL_PZN_PRICE_TOOLTIP = "Summe der Preise für eine über HiMiNr bestimmten sonstige Leistung. Nur in Verbindung mit einem HiMiNr sinnvoll";
	public static final String CRIT_SOLE_FULL_HPN_NUMBER_TOOLTIP = "Anzahl der über HPNCode bestimmten sonstigen Leistungen. Nur in Verbindung mit einem HPNCode sinnvoll";
	public static final String CRIT_SOLE_FULL_PZN_NUMBER_TOOLTIP = "Anzahl der über HiMiNr bestimmten sonstigen Leistungen. Nur in Verbindung mit einem HiMiNr sinnvoll";


// fallübergreifend
	public static final String CRIT_CASE_NUMBER_DIS = "Fallanzahl";
	public static final String CRIT_CASE_NUMBER = "Fallanzahl";
	public static final String CRIT_CASE_ADMISSIONDATE_DIS = "Fall: Aufnahmedatum";
	public static final String CRIT_CASE_ADMISSIONDATE = "FallAufnahmedatum";
	public static final String CRIT_CASE_ADMISSIONREASON_DIS = "Fall: Aufnahmegrund1";
	public static final String CRIT_CASE_ADMISSIONREASON = "FallAufnahmegrund1";
	public static final String CRIT_CASE_DRG_DIS = "Fall: DRG";
	public static final String CRIT_CASE_DRG = "FallDRG";
	public static final String CRIT_CASE_DRGPARTITION_DIS = "Fall: DRGPartition";
	public static final String CRIT_CASE_DRGPARTITION = "FallDRGPartition";
	public static final String CRIT_CASE_ADRG_DIS = "Fall: ADRG";
	public static final String CRIT_CASE_ADRG = "FallADRG";
	public static final String CRIT_CASE_MDC_DIS = "Fall: MDC";
	public static final String CRIT_CASE_MDC = "FallMDC";
	public static final String CRIT_CASE_DISCHARGEDATE_DIS = "Fall: Entlassungsdatum";
	public static final String CRIT_CASE_DISCHARGEDATE = "FallEntlassungsdatum";
	public static final String CRIT_CASE_MAIN_DIAG_DIS = "Fall: Hauptdiagnose";
	public static final String CRIT_CASE_MAIN_DIAG = "Fallhauptdiagnose";
	public static final String CRIT_CASE_IKZ_DIS = "Fall: IKZ";
	public static final String CRIT_CASE_IKZ = "FallIKZ";
	public static final String CRIT_CASE_DIAGNOSIS_DIS = "Fall: Diagnose";
	public static final String CRIT_CASE_DIAGNOSIS = "FallDiagnose";
	public static final String CRIT_CASE_AUXDIAGNOSIS_DIS = "Fall: Nebendiagnose";
	public static final String CRIT_CASE_AUXDIAGNOSIS = "FallNebendiagnose";
	public static final String CRIT_CASE_PROC_DIS = "Fall: Prozedur";
	public static final String CRIT_CASE_PROC = "FallProzedur";
//tooltips
	public static final String CRIT_CASE_NUMBER_TOOLTIP = "Anzahl der Fälle eines Patienten";
	public static final String CRIT_CASE_DRG_TOOLTIP = "DRG eines Falles";
	public static final String CRIT_CASE_DRGPARTITION_TOOLTIP = "DRGPartition eines Falles";
	public static final String CRIT_CASE_ADRG_TOOLTIP = "ADRG eines Falles";
	public static final String CRIT_CASE_MDC_TOOLTIP = "MDC eines Falles";
	public static final String CRIT_CASE_MAIN_DIAG_TOOLTIP = "Fallhauptdiagnose";
	public static final String CRIT_CASE_IKZ_TOOLTIP = "Fall IKZ";
	public static final String CRIT_CASE_PROC_TOOLTIP = "Fall Prozedur";
	public static final String CRIT_CASE_DIAGNOSIS_TOOLTIP = "Fall Diagnose";
	public static final String CRIT_CASE_AUXDIAGNOSIS_TOOLTIP = "Fall Nebendiagnose";
	public static final String CRIT_CASE_ADMISSIONDATE_TOOLTIP = "Fall Aufnahmedatum";
	public static final String CRIT_CASE_ADMISSIONREASON_TOOLTIP = "Fall Aufnahmgrund";
	public static final String CRIT_CASE_DISCHARGEDATE_TOOLTIP = "Fall Entlassungsdatum";
//labor
	public static final String CRIT_LABOR_VALUE = "Laborwert";
	public static final String CRIT_LABOR_VALUE_DIS = "Laborwert";
	public static final String CRIT_LABOR_VALUE_TOOLTIP = "Laborwert";
	public static final String CRIT_LABOR_TEXT = "Labortext";
	public static final String CRIT_LABOR_TEXT_DIS = "Labortext";
	public static final String CRIT_LABOR_TEXT_TOOLTIP = "Labortext";
	public static final String CRIT_LABOR_UNIT = "Laboreinheit";
	public static final String CRIT_LABOR_UNIT_DIS = "Labor-Einheit";
	public static final String CRIT_LABOR_UNIT_TOOLTIP = "Labor-Einheit";
	public static final String CRIT_LABOR_DESCRIPTION = "Laborbeschreibung";
	public static final String CRIT_LABOR_DESCRIPTION_DIS = "Labor-Beschreibung";
	public static final String CRIT_LABOR_DESCRIPTION_TOOLTIP = "Labor-Beschreibung";
// gruppen der kriterien
	public static final String GROUP_MOVEMENT = "Bewegungen";
	public static final String  GROUP_FEES = "Entgelte";
	public static final String  GROUP_CASE =  "Fallkriterien";
	public static final String  GROUP_CROSS_CASES = "Fallübergreifend";
	public static final String  GROUP_CODING = "Kodierung";
	public static final String  GROUP_LABOR = "Labor";
	public static final String  GROUP_MEDICIN = "Medikamente";
	public static final String  GROUP_PATIENT = "Patientendaten";
	public static final String  GROUP_SOLE = "SoLe";
	public static final String GROUP_OTHERS = "Sonstige";
	public static final String GROUP_ALL_SORTED = "Alle Kriterien, alphabetisch sortiert";

	public static final String GROUP_MOVEMENT_TOOLTIP = "Bewegungen:<ul><li>Anzahl Bewegungen </li>"
		+"<li>Fachabteilungsbewegung </li>"
		+"<li>Station </li><li>aufnehmende Fachabteilung </li>"
		+"<li>behandelnde Fachabteilung </li>"
		+"<li>entlassende Fachabteilung </li></ul>";
	public static final String  GROUP_FEES_TOOLTIP = "Entgelte:<ul><li>ADRG </li>"
		+"<li>Abschlags_CW </li>"
		+"<li>Abschlagstage </li>"
		+"<li>Anzahl Entgelte </li>"
		+ "<li>Basisfallwert</li><li>DRG </li>"
		+ "<li>Entgeltliste: Entgeltart</li>"
		+ "<li>Entgeltliste: Entgelteinzelbetrag</li>"
		+ "<li>Entgeltliste: Entgeltabrechnung von</li>"
		+ "<li>Entgeltliste: Entgeltabrechnung bis</li>"
		+ "<li>Entgeltliste: Entgeltanzahl je Entgelt</li>"
		+"<li>Entgeltschlüssel </li>"
		+"<li>Grouperrelevant: Diagnose</li>"
		+"<li>Grouperrelevant: Prozedur</li>"
		+"<li>Kostengewicht Katalog </li>"
		+"<li>kostengewicht effektive </li>"
		+"<li>MDC</li>"
		+"<li>PCCL </li>"
		+"<li>Rechnung: Rechnungsnummer </li>"
		+"<li>Rechnung: Rechnungsdatum </li>"
		+"<li>Rechnung: rechnungsart </li>"
		+"<li>Summe der Entgelte </li>"
		+"<li>Zuschlags_CW </li>"
		+"<li>Zuschlagstage </li>"
		+"<li>mittlere Verweilsdauer </li>"
		+"<li>obere Grenzverweilsdauer </li>"
		+ "<li>untere Grenzverweilsdauer </li></ul>";
	public static final String  GROUP_FEES__SUGG_TOOLTIP = "Entgelte:<ul>"
		+"<li>Anzahl Entgelte </li>"
		+ "</ul>";
	public static final String  GROUP_CASE_TOOLTIP =  "Fallkriterien:<ul><li>Aufnahmedatum </li>"
		+"<li>Aufnahmegrund1</li>"
		+"<li>Aufnahmegrund2 </li>"
		+"<li>Aufnahmejahr </li>"
		+ "<li>Aufnahmemonat</li>"
		+"<li>Aufnahmetag</li>"
		+"<li>Aufnahmeuhrzeit</li>"
		+"<li>Aufnahmewochentag </li>"
		+"<li>Beatmungsdauer </li>"
		+"<li>Einweisender Arzt </li>"
		+"<li>Einweisendes Krankenhaus </li>"
		+"<li>Entlassungsdatum </li>"
		+ "<li>Entlassungsgrund12 </li>"
		+"<li>Entlassungsgrund3 </li>"
		+"<li>Entlassungsjahr </li>"
		+"<li>Entlassungsmonat </li>"
		+"<li>Entlassungstag </li>"
		+"<li>Entlassungsuhrzeit </li>"
		+"<li>Entlassungswochentag </li>"
		+"<li>Institutionskennzeichen </li>"
		+ "<li>Intensivverweildauer </li>"
		+ "<li>Pflegestatus </li>"
		+ "<li>Psychostatus </li>"
		+ "<li>Tag der Entbindung 1 </li>"
		+ "<li>Tag der Entbindung 2 </li>"
		+ "<li>Tage ohne Berechnung </li>"
		+ "<li>Verlegungsdatum </li>"
		+ "<li>Verlegungsuhrzeit </li>"
		+"<li>Verweildauer in Stunden </li>"
		+"<li>Verweildauen kleiner 24h </li>"
		+"<li>Urlaub</li></ul>";
	public static final String  GROUP_CASE_SUGG_TOOLTIP =  "Fallkriterien:<ul>"
		+"<li>Aufnahmegrund1</li>"
		+"<li>Aufnahmegrund2 </li>"
		+"<li>Beatmungsdauer </li>"
		+ "<li>Entlassungsgrund12 </li>"
		+"<li>Entlassungsgrund3 </li>"
		+ "<li>Verweildauer </li>"
		+"<li>Verweildauer in Stunden </li>"
		+"<li>Verweildauen kleiner 24h </li></ul>";
	public static final String  GROUP_CROSS_CASES_TOOLTIP = "Fallübergreifend:<ul>"
		+"<li>Fallanzahl</li>"
		+"<li>Fall: Aufnahmedatum</li>"
		+"<li>Fall: Aufnahmegrund1</li>"
		+"<li>Fall: DRG</li>"
		+"<li>Fall: Diagnose</li>"
		+"<li>Fall: Entlassungsdatum</li>"
		+"<li>Fall: Hauptdiagnose</li>"
		+"<li>Fall: IKZ</li>"
		+"<li>Fall: Nebendiagnose</li></ul>";
	public static final String  GROUP_CODING_TOOLTIP = "Kodierung:<ul><li>Anzahl Nebendiagnosen </li>"
		+"<li>Anzahl Prozeduren </li>"
		+"<li>Anzahl gleicher Prozeduren </li>"
		+"<li>Aufnahmediagnose</li>"
		+ "<li>Diagnose </li>"
		+ "<li>Diagnose: Lokalisation</li>"
		+ "<li>Diagnose: Type</li>"
		+"<li>Hauptdiagnose </li>"
		+"<li>Hauptdiagnose: Lokalisation </li>"
		+"<li>Nebendiagnose </li>"
		+ "<li>Nebendiagnose: Lokalisation</li>"
		+ "<li>Nebendiagnose: Typ</li>"
		+"<li>OPS </li>"
		+"<li>OPSDatum </li>"
		+"<li>OPSLokalisation </li>"
		+ "<li>Sek. Diagnose</li>"
		+ "<li>Sek. Diagnose: Lokalisation</li>"
		+ "<li>Sek. Diagnose: Type</li>"
		+ "<li>Sek. Diagnose: Primär</li>"
		+ "</ul>";
	public static final String  GROUP_CODING_SUGG_TOOLTIP = "Kodierung:<ul>"
		+ "<li>Diagnose </li>"
		+"<li>Hauptdiagnose </li>"
		+"<li>Nebendiagnose </li>"
		+"<li>Prozedur </li></ul>";
	public static final String  GROUP_LABOR_TOOLTIP = "Labor:<ul>"
		+ "<li>Laborwert </li>"
		+"<li>Labortext </li>"
		+"<li>Labor-Einheit </li>"
		+"<li>Labor-Beschreibung </li></ul>";
	public static final String  GROUP_MEDICIN_TOOLTIP = "Medikamente:<ul><li>Medikament: ATC - Kode</li>"
		+ "<li>Medikament: Dosierung</li>"
		+ "<li>Medikament: Dosiereinheit</li>"
		+ "<li>Medikament: Gesamtdosis für ATC</li>"
		+ "<li>Medikament: Gesamtdosis für PZN</li>"
		+ "<li>Medikament: Gesamtdosis</li>"
		+ "<li>Medikament: Gesamtpreis für ATC</li>"
		+ "<li>Medikament: Gesamtpreis für PZN</li>"
		+ "<li>Medikament: Gesamtpreis</li>"
		+ "<li>Medikament: PZN - Kode</li>"
		+ "<li>Medikament: Preis</li>"
		+ "<li>Medikament: Verordnungsdatum</li></ul>";
	public static final String  GROUP_PATIENT_TOOLTIP = "Patientendaten:<ul><li>Alter in Jahren </li>"
		+"<li>Alter in Tagen </li>"
		+"<li>Geburtsdatum</li>"
		+"<li>Gewicht </li>"
		+"<li>Postleitzahl</li>"
		+"<li>Versichertenstatus</li>"
		+"<li>Wohnort</li>"
		+"</ul>";
	public static final String  GROUP_SOLE_TOOLTIP = "SoLe:<ul>"
		+ "<li>SoLe:&nbsp;Einzelpreis</li>"
		+ "<li>SoLe:&nbsp;Gesamtmenge für HPN</li>"
		+ "<li>SoLe:&nbsp;Gesamtmenge für Hilfsmittelnummer</li>"
		+ "<li>SoLe:&nbsp;Gesamtpreis</li>"
		+ "<li>SoLe:&nbsp;Gesamtpreis für HPN</li>"
		+ "<li>SoLe:&nbsp;Gesamtpreis für Hilfsmittelnummer</li>"
		+ "<li>SoLe:&nbsp;HPN&nbsp;-&nbsp;Kode</li>"
		+ "<li>SoLe:&nbsp;Hilfsmittelnummer</li>"
		+ "<li>SoLe:&nbsp;Menge</li>"
		+ "<li>SoLe:&nbsp;Verordnungsdatum</li></ul>";
	public static final String GROUP_OTHERS_TOOLTIP = "Sonstige:<ul>"
		+ "<li>Zeitangabe 'Jetzt'</li>"
		+ "<li>Vorschlag</li>"
		+ "<li>Fall.Numeric1</li>"
		+ "<li>Fall.Numeric2</li><li>Fall.Numeric3</li><li>Fall.Numeric4</li><li>Fall.Numeric5</li>"
		+ "<li>Fall.String1</li><li>Fall.String2</li><li>Fall.String3</li><li>Fall.String4</li><li>Fall.String5</li></ul>";
	public static final String GROUP_ALL_SORTED_TOOLTIP = "Liste aller Kriterien, sortiert nach der resten Buchstabe";
	/**
	 ** types of operation
	 */
	public static final int OPTYPE_ALL = 0;
	public static final int OPTYPE_NUMERIC = 1;
	public static final int OPTYPE_NUMERIC_TABLE = 2;
	public static final int OPTYPE_NUMERIC_ONLY = 3;
	public static final int OPTYPE_EQUAL = 4;
	public static final int OPTYPE_EQUAL_ONLY = 5;
	public static final int OPTYPE_COMPARE = 6;
	public static final int OPTYPE_COMPARE_TABLE = 7;
	public static final int OPTYPE_COMPARE_ONLY = 8;
	public static final int OPTYPE_TABLES = 9;
	public static final int OPTYPE_TABLES_CASE = 10;
	public static final int OPTYPE_DATE = 11;
	public static final int OPTYPE_NOTHING = 99;

// operation names
	/*
	public static final String OP_NO_OPERATION = "keine Operation";
	public static final String OP_AND = "und ( && )";
	public static final String OP_OR = "oder ( || )";
	public static final String OP_EQUAL = "gleich ( == )";
	public static final String OP_GT = "größer ( > )";
	public static final String OP_GT_EQUAL = "größer gleich ( >= )";
	public static final String OP_LT = "kleiner ( < )";
	public static final String OP_LT_EQUAL = "kleiner gleich ( <= )";
	public static final String OP_NOT_EQUAL = "ungleich ( != )";
	public static final String OP_PLUS = "plus ( + )";
	public static final String OP_MINUS = "minus ( - )";
	public static final String OP_MULTIPL = "multipliziert ( * )";
	public static final String OP_DIVIDE = "geteilt durch ( / )";
	public static final String OP_IN = "enthalten ( IN  )";
	public static final String OP_NOT_IN = "keine enthalten ( NOT IN )";
	public static final String OP_NOT_IN_TABLE = "keine enthalten in Tabelle ( NOT IN @ )";
	public static final String OP_IN_TABLE = "in Tabelle ( @ )";
	public static final String OP_IN_FALL = "in Fall ( ~ )";
	public static final String OP_NOT_DOUBLE_IN = "nicht doppelt in ( !! )";
	public static final String OP_NOT_DOUBLE_IN_TABLE = "nicht doppelt in Tabelle ( !! @ )";
	public static final String OP_MANY_IN = "mehrere in ( ## )";
	public static final String OP_MANY_IN_TABLE = "mehrere in Tabelle ( #@ )";
	public static final String OP_CONCATENATE = "verketten ( | )";
	*/

	// Bewgungskriterien
	private static DatRulesCriterion[] m_movementCritList = new DatRulesCriterion[] {
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_DEPARTMENT, "Abteilung", String.class, false, CRIT_DEPARTMENT_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_MOVEMENTS_COUNT, "AnzahlAbteilungen", Integer.class, false, CRIT_MOVEMENTS_COUNT_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_STATION, CRIT_STATION, String.class, false, CRIT_STATION_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_RELEASE_DEPARTMENT_DIS, CRIT_RELEASE_DEPARTMENT, String.class, false, CRIT_RELEASE_DEPARTMENT_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_COMBAT_DEPARTMENT_DIS, CRIT_COMBAT_DEPARTMENT, String.class, false, CRIT_COMBAT_DEPARTMENT_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_RECEIPT_DEPARTMENT_DIS, CRIT_RECEIPT_DEPARTMENT, String.class, false, CRIT_RECEIPT_DEPARTMENT_TOOLTIP),
	};
	// Entgelte
	private static DatRulesCriterion[] m_feeCritList = new DatRulesCriterion[] {
		new DatRulesCriterion(OPTYPE_COMPARE, CRIT_BASE_VALUE, Double.class, false, CRIT_BASE_VALUE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_DRG, String.class, false, CRIT_DRG_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_DRGPARTITION, String.class, false, CRIT_DRGPARTITION_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_ADRG, String.class, false, CRIT_ADRG_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_MDC, String.class, false, CRIT_MDC_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_DIAGNOSE_GROUPED_DIS, CRIT_DIAGNOSE_GROUPED, String.class, false, CRIT_DIAGNOSE_GROUPED_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_PROCEDURE_GROUPED_DIS, CRIT_PROCEDURE_GROUPED, String.class, false, CRIT_PROCEDURE_GROUPED_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_TOP_LENGTH_OF_STAY, "obere_Grenzverweildauer", Integer.class, false, CRIT_TOP_LENGTH_OF_STAY_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_MID_LENGTH_OF_STAY, "mittlere_Verweildauer", Double.class, false, CRIT_MID_LENGTH_OF_STAY_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_BOTTOM_LENGTH_OF_STAY, "untere_Grenzverweildauer", Integer.class, false, CRIT_BOTTOM_LENGTH_OF_STAY_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_REDUCTION_DAYS, Integer.class, false, CRIT_REDUCTION_DAYS_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_BONUS_DAYS, Integer.class, false, CRIT_BONUS_DAYS_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_CW_CATALOG, "Kostengewicht_Katalog", Double.class, false, CRIT_CW_CATALOG_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_CW_EFFECTIV, "Kostengewicht_effektiv", Double.class, false, CRIT_CW_EFFECTIV_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_REDUCTION_CW, Double.class, false, CRIT_REDUCTION_CW_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_BONUS_CW, Double.class, false, CRIT_BONUS_CW_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE, CRIT_PCCL, Integer.class, false, CRIT_PCCL_TOOLTIP),
// Entgeltliste
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_ENTGELTART, String.class, false, CRIT_ENTGELTART_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_ENTGELTEINZELBETRAG,  Double.class, false, CRIT_ENTGELTEINZELBETRAG_TOOLTIP),
		new DatRulesCriterion(OPTYPE_DATE, CRIT_ENTGELTABRECHNUNG_VON_DIS, CRIT_ENTGELTABRECHNUNG_VON, Date.class, false, CRIT_ENTGELTABRECHNUNG_VON_TOOLTIP),
		new DatRulesCriterion(OPTYPE_DATE, CRIT_ENTGELTABRECHNUNG_BIS_DIS, CRIT_ENTGELTABRECHNUNG_BIS, Date.class, false, CRIT_ENTGELTABRECHNUNG_BIS_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_ENTGELTANZAHL_DIS, CRIT_ENTGELTANZAHL, Integer.class, false, CRIT_ENTGELTANZAHL_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_TAGE_OHNE_BERECHNUNG_PRO_ENTGELT_DIS, CRIT_TAGE_OHNE_BERECHNUNG_PRO_ENTGELT, Integer.class, false, CRIT_TAGE_OHNE_BERECHNUNG_PRO_ENTGELT_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_FEE_SUM, "EntgeltSumme", Double.class, false, CRIT_FEE_SUM_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_ENTGELTSUMMEJEENTGELT_DIS, CRIT_ENTGELTSUMMEJEENTGELT, Double.class, false, CRIT_ENTGELTSUMMEJEENTGELT_TOOLTIP),
// Rechnung
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_RECHNUNG_NUMMER, String.class, false, CRIT_RECHNUNG_NUMMER_TOOLTIP),
		new DatRulesCriterion(OPTYPE_DATE, CRIT_RECHNUNG_DATUM, CRIT_RECHNUNG_DATUM, Date.class, false, CRIT_RECHNUNG_DATUM_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES,  CRIT_RECHNUNG_ART, String.class, false, CRIT_RECHNUNG_ART_TOOLTIP),

	};
	// Fallkriterien
	private static DatRulesCriterion[] m_caseCritList = new DatRulesCriterion[] {
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_INSTITUTE, String.class, false, CRIT_INSTITUTE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_ADMISSION_DOC_DIS, CRIT_ADMISSION_DOC, String.class, false, CRIT_ADMISSION_DOC_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_ADMISSION_HOSPITAL_DIS, CRIT_ADMISSION_HOSPITAL, String.class, false, CRIT_ADMISSION_HOSPITAL_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_ADM_CAUSE, CRIT_ADM_CAUSE, String.class, false, CRIT_ADM_CAUSE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_HEALTH_ENSURANCE, String.class, false, CRIT_HEALTH_ENSURANCE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_BREATHING, Integer.class, CRIT_BREATHING_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_VWD, Integer.class, CRIT_VWD_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_VWD_IN_HOURS, "VerweildauerInStunden", Integer.class, CRIT_VWD_IN_HOURS_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_LOSLESS24h, "AufenthaltKleiner24h", Integer.class, CRIT_LOSLESS24h_TOOLTIP),
		new DatRulesCriterion(OPTYPE_DATE, CRIT_ADMISSION_DATE, Date.class, false, CRIT_ADMISSION_DATE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE, CRIT_ADMISSION_JEAR, Integer.class, false, CRIT_ADMISSION_JEAR_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE, CRIT_ADMISSION_MONTH, Integer.class, false, CRIT_ADMISSION_MONTH_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE, CRIT_ADMISSION_DAY, Integer.class, false, CRIT_ADMISSION_DAY_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE_ONLY, CRIT_ADMISSION_DAY_TIME_DIS, CRIT_ADMISSION_DAY_TIME, String.class, false, CRIT_ADMISSION_DAY_TIME_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE_TABLE, CRIT_ADMISSION_WEEK_DAY, Integer.class, false, CRIT_ADMISSION_WEEK_DAY_TOOLTIP),
		new DatRulesCriterion(OPTYPE_DATE, CRIT_SEPARATION_DATE, Date.class, false, CRIT_SEPARATION_DATE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE, CRIT_SEPARATION_YEAR, Integer.class, false, CRIT_SEPARATION_YEAR_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE, CRIT_SEPARATION_MONTH, Integer.class, false, CRIT_SEPARATION_MONTH_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE, CRIT_SEPARATION_DAY, Integer.class, false, CRIT_SEPARATION_DAY_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE_ONLY, CRIT_SEPARATION_DAY_TIME, String.class, false, CRIT_SEPARATION_DAY_TIME_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE_TABLE, CRIT_SEPARATION_WEEK_DAY, Integer.class, false, CRIT_SEPARATION_WEEK_DAY_TOOLTIP),
		new DatRulesCriterion(OPTYPE_DATE, CRIT_TRANSFER_DATE, Date.class, false, CRIT_TRANSFER_DATE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE_ONLY, CRIT_TRANSFER_DAY_TIME, String.class, false, CRIT_TRANSFER_DAY_TIME_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_ADM_TYPE, "Aufnahmegrund1", Integer.class, CRIT_ADM1_TYPE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_ADM2_TYPE, "Aufnahmegrund2", Integer.class, CRIT_ADM2_TYPE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_DIS_TYPE, "Entlassungsgrund12", Integer.class, CRIT_DIS_TYPE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_DIS_TYPE3, "Entlassungsgrund3", Integer.class, CRIT_DIS_TYPE3_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_INTENSIV_STAY, Integer.class, false, CRIT_INTENSIV_STAY_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_CARE_STATE, Integer.class, false, CRIT_CARE_STATE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_PSYCHO_STATE, Integer.class, false, CRIT_PSYCHO_STATE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_VOLUNTARY_DAYS_DIS, CRIT_VOLUNTARY_DAYS, Integer.class, false, CRIT_VOLUNTARY_DAYS_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, "Urlaub", Integer.class, false, "Anzahl der Urlaubstagen"),
		new DatRulesCriterion(OPTYPE_DATE, CRIT_ACCOUCHEMENT_DATE_1_DIS, CRIT_ACCOUCHEMENT_DATE_1, Date.class, false, CRIT_ACCOUCHEMENT_DATE_1_TOOLTIP),
		new DatRulesCriterion(OPTYPE_DATE, CRIT_ACCOUCHEMENT_DATE_2_DIS, CRIT_ACCOUCHEMENT_DATE_2, Date.class, false, CRIT_ACCOUCHEMENT_DATE_2_TOOLTIP),
	};
	// Kodierung
	private static DatRulesCriterion[] m_codingCritList = new DatRulesCriterion[] {
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_PRINC_DIAG, String.class, CRIT_PRINC_DIAG_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL_ONLY, CRIT_PRINC_DIAG_LOC_DIS, CRIT_PRINC_DIAG_LOC, Integer.class, false, CRIT_PRINC_DIAG_LOC_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_ADMISSION_DIAG, String.class, false, CRIT_ADMISSION_DIAG_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_AUX_DIAG, false, String.class, CRIT_AUX_DIAG_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_DIAG, false, String.class, CRIT_DIAG_TOOLTIP),

		new DatRulesCriterion(OPTYPE_EQUAL_ONLY, CRIT_AUX_DIAG_LOC_DIS, CRIT_AUX_DIAG_LOC, Integer.class, false,  CRIT_AUX_DIAG_LOC_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL_ONLY, CRIT_AUX_DIAG_TYPE_DIS, CRIT_AUX_DIAG_TYPE, Integer.class, false,  CRIT_AUX_DIAG_TYPE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL_ONLY, CRIT_DIAG_LOC_DIS, CRIT_DIAG_LOC, Integer.class, false,  CRIT_DIAG_LOC_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL_ONLY, CRIT_DIAG_TYPE_DIS , CRIT_DIAG_TYPE, Integer.class, false,  CRIT_DIAG_TYPE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_SEC_DIAG_DIS, CRIT_SEC_DIAG, String.class, false,  CRIT_SEC_DIAG_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL_ONLY, CRIT_SEC_DIAG_LOC_DIS, CRIT_SEC_DIAG_LOC, Integer.class, false,  CRIT_SEC_DIAG_LOC_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL_ONLY, CRIT_SEC_DIAG_TYPE_DIS, CRIT_SEC_DIAG_TYPE, Integer.class, false,  CRIT_SEC_DIAG_TYPE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_SEC_DIAG_PRIM_DIS, CRIT_SEC_DIAG_PRIM, String.class, false,  CRIT_SEC_DIAG_PRIM_TOOLTIP),

		new DatRulesCriterion(OPTYPE_TABLES, CRIT_PROC, false, String.class, CRIT_PROC_TOOLTIP),
		new DatRulesCriterion(OPTYPE_DATE, CRIT_PROC_DATE, false, Date.class, false, CRIT_PROC_DATE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL_ONLY, CRIT_PROC_LOCALISATION, Integer.class, false, CRIT_PROC_LOCALISATION_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_PROC_NUMBER, "AnzahlProzeduren", Integer.class, false,CRIT_PROC_NUMBER_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_SDX_NUMBER, "AnzahlNebendiagnosen", Integer.class, false, CRIT_SDX_NUMBER_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_EQUAL_PROC_NUMBER, "GleichenProzedurenAnzahl", Integer.class, false, CRIT_EQUAL_PROC_NUMBER_TOOLTIP),
	};
	// Patientendaten
	private static DatRulesCriterion[] m_patCrit = new DatRulesCriterion[] {
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_AGE_YEARS_DIS, CRIT_AGE_YEARS, Integer.class, CRIT_AGE_YEARS_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_AGE_MONTHS_DIS, CRIT_AGE_MONTHS, Integer.class, CRIT_AGE_MONTHS_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL_ONLY, CRIT_GENDER, Integer.class, CRIT_GENDER_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_WEIGHT, Double.class, CRIT_WEIGHT_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL_ONLY, CRIT_INSURANCE_STATUS, Integer.class, CRIT_INSURANCE_STATUS_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_ZIP_CODE, String.class, CRIT_ZIP_CODE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_CITY , String.class, CRIT_CITY_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE_ONLY, CRIT_BIRTHDAY_DIS, CRIT_BIRTHDAY,String.class, false, CRIT_BIRTHDAY_TOOLTIP),
	};

	// Sonstige
	private static DatRulesCriterion[] othersCritList = new DatRulesCriterion[] {
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_NOW_TIME, String.class, false, CRIT_NOW_TIME_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_SUGGESTION_FLAG, Integer.class, false, CRIT_SUGGESTION_FLAG_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_CASE_NUMERIC1, Integer.class, false, CRIT_CASE_NUMERIC1_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_CASE_NUMERIC2, Integer.class, false, CRIT_CASE_NUMERIC2_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_CASE_NUMERIC3, Integer.class, false, CRIT_CASE_NUMERIC3_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_CASE_NUMERIC4, Integer.class, false, CRIT_CASE_NUMERIC4_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_CASE_NUMERIC5, Integer.class, false, CRIT_CASE_NUMERIC5_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_CASE_STR1, String.class, false, CRIT_CASE_STR1_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_CASE_STR2, String.class, false, CRIT_CASE_STR2_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_CASE_STR3, String.class, false, CRIT_CASE_STR3_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_CASE_STR4, String.class, false, CRIT_CASE_STR4_TOOLTIP),
		new DatRulesCriterion(OPTYPE_EQUAL, CRIT_CASE_STR5, String.class, false, CRIT_CASE_STR5_TOOLTIP),
	};
// fallübergrefende Kirterien
	private static DatRulesCriterion[] m_mixCaseCritList = new DatRulesCriterion[] {
		new DatRulesCriterion(OPTYPE_COMPARE_ONLY, CRIT_CASE_ADMISSIONDATE_DIS, CRIT_CASE_ADMISSIONDATE,String.class, false, CRIT_CASE_ADMISSIONDATE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_CASE_NUMBER_DIS, CRIT_CASE_NUMBER,Integer.class, false, CRIT_CASE_NUMBER_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC, CRIT_CASE_ADMISSIONREASON_DIS, CRIT_CASE_ADMISSIONREASON,Integer.class, false, CRIT_CASE_ADMISSIONREASON_TOOLTIP),
		new DatRulesCriterion(OPTYPE_COMPARE_ONLY, CRIT_CASE_DISCHARGEDATE_DIS, CRIT_CASE_DISCHARGEDATE, String.class, false, CRIT_CASE_DISCHARGEDATE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_CASE_DRG_DIS, CRIT_CASE_DRG, String.class, false, CRIT_CASE_DRG_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_CASE_DRGPARTITION_DIS, CRIT_CASE_DRGPARTITION, String.class, false, CRIT_CASE_DRGPARTITION_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_CASE_ADRG_DIS, CRIT_CASE_ADRG, String.class, false, CRIT_CASE_ADRG_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_CASE_MDC_DIS, CRIT_CASE_MDC, String.class, false, CRIT_CASE_MDC_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_CASE_MAIN_DIAG_DIS, CRIT_CASE_MAIN_DIAG, String.class, false, CRIT_CASE_MAIN_DIAG_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_CASE_IKZ_DIS, CRIT_CASE_IKZ, String.class, false, CRIT_CASE_IKZ_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_CASE_DIAGNOSIS_DIS, CRIT_CASE_DIAGNOSIS, String.class, false, CRIT_CASE_DIAGNOSIS_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_CASE_AUXDIAGNOSIS_DIS, CRIT_CASE_AUXDIAGNOSIS, String.class, false, CRIT_CASE_AUXDIAGNOSIS_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_CASE_PROC_DIS, CRIT_CASE_PROC, String.class, false, CRIT_CASE_PROC_TOOLTIP),

	};
	private static DatRulesCriterion[]m_labCritList = new DatRulesCriterion[]{
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_LABOR_VALUE_DIS, CRIT_LABOR_VALUE, Double.class, false, CRIT_LABOR_VALUE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_LABOR_TEXT_DIS, CRIT_LABOR_TEXT, String.class, false, CRIT_LABOR_TEXT_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_LABOR_UNIT_DIS, CRIT_LABOR_UNIT, String.class, false, CRIT_LABOR_UNIT_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_LABOR_DESCRIPTION_DIS, CRIT_LABOR_DESCRIPTION, String.class, false, CRIT_LABOR_DESCRIPTION_TOOLTIP),

	};
	private static DatRulesCriterion[] m_medCritList = new DatRulesCriterion[] {

		new DatRulesCriterion(OPTYPE_TABLES, CRIT_MEDICAMENT_ATC_CODE_DIS, CRIT_MEDICAMENT_ATC_CODE, String.class, false, CRIT_MEDICAMENT_ATC_CODE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_MEDICAMENT_PZN_CODE_DIS, CRIT_MEDICAMENT_PZN_CODE, String.class, false, CRIT_MEDICAMENT_PZN_CODE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_MEDICAMENT_PERSCRIPT_DOSAGE_UNIT_DIS,
		CRIT_MEDICAMENT_PERSCRIPT_DOSAGE_UNIT, String.class, false, CRIT_MEDICAMENT_PERSCRIPT_DOSAGE_UNIT_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_MEDICAMENT_PERSCRIPT_DATE_DIS, CRIT_MEDICAMENT_PERSCRIPT_DATE,
		String.class, false, CRIT_MEDICAMENT_PERSCRIPT_DATE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_MEDICAMENT_PRICE_DIS, CRIT_MEDICAMENT_PRICE, Double.class, false, CRIT_MEDICAMENT_PRICE_TOOLTIP),

		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_MEDICAMENT_PERSCRIPT_DOSAGE_DIS,
		CRIT_MEDICAMENT_PERSCRIPT_DOSAGE, Double.class, false, CRIT_MEDICAMENT_PERSCRIPT_DOSAGE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_MEDICAMENT_FULL_PRICE_DIS, CRIT_MEDICAMENT_FULL_PRICE, Double.class, false, CRIT_MEDICAMENT_FULL_PRICE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_MEDICAMENT_FULL_PRICE_ATC_DIS, CRIT_MEDICAMENT_FULL_PRICE_ATC,
		Double.class, false, CRIT_MEDICAMENT_FULL_PRICE_ATC_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_MEDICAMENT_FULL_PRICE_PZN_DIS, CRIT_MEDICAMENT_FULL_PRICE_PZN,
		Double.class, false, CRIT_MEDICAMENT_FULL_PRICE_PZN_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_MEDICAMENT_FULL_DOSAGE_ATC_DIS, CRIT_MEDICAMENT_FULL_DOSAGE_ATC,
		Double.class, false, CRIT_MEDICAMENT_FULL_DOSAGE_ATC_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_MEDICAMENT_FULL_DOSAGE_PZN_DIS, CRIT_MEDICAMENT_FULL_DOSAGE_PZN,
		Double.class, false, CRIT_MEDICAMENT_FULL_DOSAGE_PZN_TOOLTIP),
	};

// SoLe
	private static DatRulesCriterion[] m_soleCritList = new DatRulesCriterion[] {
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_SOLE_HPN_DIS, CRIT_SOLE_HPN, String.class, false, CRIT_SOLE_HPN_TOOLTIP),
		new DatRulesCriterion(OPTYPE_TABLES, CRIT_SOLE_HIMINUMBER_DIS, CRIT_SOLE_HIMINUMBER, String.class, false, CRIT_SOLE_HIMINUMBER_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_SOLE_PRICE_DIS, CRIT_SOLE_PRICE, Double.class, false, CRIT_SOLE_PRICE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_SOLE_FULL_PRICE_DIS, CRIT_SOLE_FULL_PRICE, Double.class, false, CRIT_SOLE_FULL_PRICE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_SOLE_PRESCRIPT_DATE_DIS, CRIT_SOLE_PRESCRIPT_DATE, String.class, false, CRIT_SOLE_PRESCRIPT_DATE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_SOLE_NUMBER_DIS, CRIT_SOLE_NUMBER, Double.class, false, CRIT_SOLE_NUMBER_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_SOLE_FULL_HPN_PRICE_DIS, CRIT_SOLE_FULL_HPN_PRICE, Double.class, false, CRIT_SOLE_FULL_HPN_PRICE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_SOLE_FULL_PZN_PRICE_DIS, CRIT_SOLE_FULL_PZN_PRICE, Double.class, false, CRIT_SOLE_FULL_PZN_PRICE_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_SOLE_FULL_HPN_NUMBER_DIS, CRIT_SOLE_FULL_HPN_NUMBER, Double.class, false, CRIT_SOLE_FULL_HPN_NUMBER_TOOLTIP),
		new DatRulesCriterion(OPTYPE_NUMERIC_ONLY, CRIT_SOLE_FULL_PZN_NUMBER_DIS, CRIT_SOLE_FULL_PZN_NUMBER, Double.class, false, CRIT_SOLE_FULL_PZN_NUMBER_TOOLTIP),
	};

	public static DatRulesCriterion noCrit = new DatRulesCriterion(OPTYPE_NOTHING, CRIT_NO_CRIT, "", Double.class, false, CRIT_NO_CRIT_TOOLTIP);

	private static Object[][] allCrit =
		   {
				{new DatRulesCriterion(OPTYPE_NOTHING, GROUP_MOVEMENT, "", Double.class, false, GROUP_MOVEMENT_TOOLTIP), m_movementCritList},
				{new DatRulesCriterion(OPTYPE_NOTHING, GROUP_FEES, "",	Double.class, false, GROUP_FEES_TOOLTIP, GROUP_FEES__SUGG_TOOLTIP), m_feeCritList},
				{new DatRulesCriterion(OPTYPE_NOTHING, GROUP_CASE, "", Double.class, false, GROUP_CASE_TOOLTIP, GROUP_CASE_SUGG_TOOLTIP), m_caseCritList},
				{new DatRulesCriterion(OPTYPE_NOTHING, GROUP_CROSS_CASES, "", Double.class, false, GROUP_CROSS_CASES_TOOLTIP), m_mixCaseCritList},
				{new DatRulesCriterion(OPTYPE_NOTHING, GROUP_CODING, "", Double.class, false, GROUP_CODING_TOOLTIP, GROUP_CODING_SUGG_TOOLTIP), m_codingCritList},
				{new DatRulesCriterion(OPTYPE_NOTHING, GROUP_LABOR, "", Double.class, false, GROUP_LABOR_TOOLTIP), m_labCritList},
				{new DatRulesCriterion(OPTYPE_NOTHING, GROUP_MEDICIN, "", Double.class, false, GROUP_MEDICIN_TOOLTIP), m_medCritList},
				{new DatRulesCriterion(OPTYPE_NOTHING, GROUP_PATIENT, "", Double.class, false, GROUP_PATIENT_TOOLTIP), m_patCrit},
				{new DatRulesCriterion(OPTYPE_NOTHING, GROUP_SOLE, "", Double.class, false, GROUP_SOLE_TOOLTIP), m_soleCritList},
				{new DatRulesCriterion(OPTYPE_NOTHING, GROUP_OTHERS, "", Double.class, false, GROUP_OTHERS_TOOLTIP), othersCritList},
				{noCrit, null},
	};


//   intervals
/*
	private static DatRulesCriterion[] m_sysdateIntervals = new DatRulesCriterion[] {
		new DatRulesCriterion(0, "keine Angabe", NOTHING, false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Datum", "Date", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Tag(e)", "days", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Monat(e)", "months", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Quartal(e)", "quater", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Jahr(e)", "years", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Aufnahmedatum", "admDate", true, Integer.class, false, ""),
		new DatRulesCriterion(0, "Entlassungsdatum", "disDate", true, Integer.class, false, ""),
	};

	private static DatRulesCriterion[] m_dateIntervals = new DatRulesCriterion[] {
		new DatRulesCriterion(0, "keine Angabe", NOTHING, false, Integer.class, false, ""),
		new DatRulesCriterion(0, "jetzt", "sysDate", true, Integer.class, false, ""),
		new DatRulesCriterion(0, "Datum", "Date", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Tag(e)", "days", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Monat(e)", "months", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Quartal(e)", "quater", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Jahr(e)", "years", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Aufnahmedatum", "admDate", true, Integer.class, false, ""),
		new DatRulesCriterion(0, "Entlassungsdatum", "disDate", true, Integer.class, false, ""),
	};

	private static DatRulesCriterion[] m_admDateIntervals = new DatRulesCriterion[] {
		new DatRulesCriterion(0, "keine Angabe", NOTHING, false, Integer.class, false, ""),
		new DatRulesCriterion(0, "jetzt", "sysDate", true, Integer.class, false, ""),
		new DatRulesCriterion(0, "Datum", "Date", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Tag(e)", "days", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Monat(e)", "months", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Quartal(e)", "quater", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Jahr(e)", "years", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Entlassungsdatum", "disDate", true, Integer.class, false, ""),
		new DatRulesCriterion(0, "OGVD", "ogvd", true, Integer.class, false, ""),
	};

	private static DatRulesCriterion[] m_disDateIntervals = new DatRulesCriterion[] {
		new DatRulesCriterion(0, "keine Angabe", NOTHING, false, Integer.class, false, ""),
		new DatRulesCriterion(0, "jetzt", "sysDate", true, Integer.class, false, ""),
		new DatRulesCriterion(0, "Datum", "Date", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Tag(e)", "days", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Monat(e)", "months", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Quartal(e)", "quater", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Jahr(e)", "years", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Aufnahmedatum", "admDate", true, Integer.class, false, ""),
	};

	private static DatRulesCriterion[] m_noInterval = new DatRulesCriterion[]{
		new DatRulesCriterion(0, "keine Angabe", NOTHING, false, Integer.class, false, ""),
	};

	private static DatRulesCriterion[] m_actCaseIntervals = new DatRulesCriterion[] {
		new DatRulesCriterion(0, "keine Angabe", NOTHING, false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Fall", "case", false, Integer.class, false, ""),
	};

	private static Object[][] m_ruleForIntervals = new Object[][] {
		{new DatRulesCriterion(0, "keine Angabe", NOTHING, false, Integer.class, false, ""), m_noInterval},
		{new DatRulesCriterion(0, "jetzt", "sysDate", true, Integer.class, false, ""), m_sysdateIntervals},
		{new DatRulesCriterion(0, "Datum", "Date", false, Integer.class, false, ""), m_dateIntervals},
		{new DatRulesCriterion(0, "Aufnahmedatum", "admDate", true, Integer.class, false, ""), m_admDateIntervals},
		{new DatRulesCriterion(0, "Entlassungsdatum", "disDate", true, Integer.class, false, ""), m_disDateIntervals},
	};

	private static Object[][] actualCase = {{new DatRulesCriterion(0, "Aktueller Fall", "actCase", true, Integer.class, false, ""), m_actCaseIntervals},};

*/


/*
	private static DatRulesCriterion[] m_rulesIntervals = new DatRulesCriterion[] {
		new DatRulesCriterion(0, "jetzt", "sysDate", true, Integer.class, false, ""),
		new DatRulesCriterion(0, "Datum", "Date", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Tag(e)", "days", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Monat(e)", "months", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Quartal(e)", "quater", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Jahr(e)", "years", false, Integer.class, false, ""),
		new DatRulesCriterion(0, "Aufnahmedatum", "admDate", true, Integer.class, false, ""),
		new DatRulesCriterion(0, "Entlassungsdatum", "disDate", true, Integer.class, false, ""),
		new DatRulesCriterion(0, "Aktueller Fall", "actCase", true, Integer.class, false, ""),
		new DatRulesCriterion(0, "Fall", "caseToActual", false, Integer.class, false, ""),
	};
*/
	/*
	public static final DatRulesAction[] m_actList0 = new DatRulesAction[] {
		new DatRulesAction(SUGG_NULL, "nichts", "Keine Aktion"),
	};

	public static final DatRulesAction[] m_actList = new DatRulesAction[] {
		new DatRulesAction(SUGG_DELETE, "delete", "Löschen (0)"),
		new DatRulesAction(SUGG_ADD, "add", "Hinzufügen (1)"),
		new DatRulesAction(SUGG_CHANGE, "change", "Ändern (2)")
	};
*/
	public static final String RULES_BUTTON_HIDE = "Ausblenden";
	public static final String RULES_BUTTON_SHOW = "Einblenden";


	public static final String[] m_rulesStates = new String[] {
												 DatCaseRuleAttributes.RULES_TYPE_ERROR, DatCaseRuleAttributes.RULES_TYPE_WARNING, DatCaseRuleAttributes.RULES_TYPE_SUGGESTION
	};

	public DatCaseRuleMgr()
	{
	}

	public static Object[][] getTreeRulesCriterion(boolean addAll)
	{
		LinkedHashMap all = new LinkedHashMap();
		Vector allSorted = new Vector();
		Comparator comp = new Comparator()
				{
					public int compare(Object o1, Object o2)
					{
						DatRulesCriterion sr1 = (DatRulesCriterion)o1;
						DatRulesCriterion sr2 = (DatRulesCriterion)o2;
						return true ? sr1.m_displayText.compareTo(sr2.m_displayText) :
							sr2.m_displayText.compareTo(sr1.m_displayText);
					}

					public boolean equals(Object obj)
					{
						return true;
					}
				};

		for(int i = 0; i < allCrit.length; i++) {
			DatRulesCriterion[] line = (DatRulesCriterion[])allCrit[i][1];
			DatRulesCriterion groupNode = (DatRulesCriterion)allCrit[i][0];
			Vector v = new Vector();
			if(line == null) {
				if(((DatRulesCriterion)allCrit[i][0]).m_displayText.equals(CRIT_NO_CRIT)) {
					all.put(allCrit[i][0], v);
				}
				continue;
			}
			for(int j = 0; j < line.length; j++) {
				DatRulesCriterion crit = (DatRulesCriterion)line[j];
				v.add(crit);
				if(addAll)
					allSorted.add(crit);
			}
			if(v.size() > 0) {
				Collections.sort(v, comp);
				all.put(allCrit[i][0], v);
			}
		}
		if(addAll && allSorted.size() > 0){
			Collections.sort(allSorted, comp);
			DatRulesCriterion group = new DatRulesCriterion(OPTYPE_NOTHING, GROUP_ALL_SORTED, "", Double.class, false, GROUP_ALL_SORTED_TOOLTIP);

			/* 3.9.5 2015-09-01 DNi: #FINDBUGS - DatRulesCriterion besitzt keine hashcode-Methode! 
			 * Bug: de.checkpoint.server.data.caseRules.DatRulesCriterion doesn't define a 
			 * hashCode() method but is used in a hashed data structure in 
			 * de.checkpoint.server.data.caseRules.DatCaseRuleMgr.getTreeRulesCriterion(boolean)
			 */
			all.put(group, allSorted);
		}
		Object[][] crits = new Object[all.size()][2];
		Set e = all.keySet();
		Iterator itr = e.iterator();
		int i = 0;
		while(itr.hasNext()) {
			Object key = itr.next();
			crits[i][0] = key;
			try {
				Vector v = (Vector)all.get(key);
				DatRulesCriterion[] line = new DatRulesCriterion[v.size()];
				for(int j = 0; j < v.size(); j++) {
					line[j] = (DatRulesCriterion)v.elementAt(j);
				}
				crits[i][1] = line;
				i++;
			} catch(Exception e1) {

			}
		}

		return crits;
	}
/*
	public static Object[][] getIntervalCriterion()
	{
		return m_rulesIntervals;
	}
*/

	public static DatRulesCriterion getCriterionByWorkText(String text)
	{
		for(int i = 0; i < allCrit.length; i++) {
			DatRulesCriterion[] line = (DatRulesCriterion[])allCrit[i][1];
			if(line == null) {
				continue;
			}
			for(int j = 0; j < line.length; j++) {
				DatRulesCriterion crit = (DatRulesCriterion)line[j];
				if(crit.getWorkText().equals(text)) {
					return crit;
				}
			}
		}
		/*		if(CMClientManager.getClientManager().isMedicineAllowed()) {
		   for(int i = 0; i < m_medCritList.length; i++) {
			DatRulesCriterion crit = m_medCritList[i];
			if(crit.m_workText.equals(text)) {
			 return crit;
			}
		   }
		   for(int i = 0; i < m_soleCritList.length; i++) {
			DatRulesCriterion crit = m_soleCritList[i];
			if(crit.m_workText.equals(text)) {
			 return crit;
			}
		   }
		  }*/
		return null;
	}

	public static Object[][] getRulesSuggCriterion()
	{
		LinkedHashMap all = new LinkedHashMap();

		for(int i = 0; i < allCrit.length; i++) {
			DatRulesCriterion[] line = (DatRulesCriterion[])allCrit[i][1];
			DatRulesCriterion groupNode = (DatRulesCriterion)allCrit[i][0];
			Vector v = new Vector();
			if(line == null) {
				if(((DatRulesCriterion)allCrit[i][0]).m_displayText.equals(CRIT_NO_CRIT)) {
					all.put(allCrit[i][0], v);
				}
				continue;
			}
			for(int j = 0; j < line.length; j++) {
				DatRulesCriterion crit = (DatRulesCriterion)line[j];
				if(crit.m_isNested) {
					v.add(crit);
				}
			}
			if(v.size() > 0) {
				Collections.sort(v, new Comparator()
				{
					public int compare(Object o1, Object o2)
					{
						DatRulesCriterion sr1 = (DatRulesCriterion)o1;
						DatRulesCriterion sr2 = (DatRulesCriterion)o2;
						return true ? sr1.m_displayText.compareTo(sr2.m_displayText) :
							sr2.m_displayText.compareTo(sr1.m_displayText);
					}

					public boolean equals(Object obj)
					{
						return true;
					}
				});
				all.put(allCrit[i][0], v);
			}
		}

		Object[][] crits = new Object[all.size()][2];
		Set e = all.keySet();
		Iterator itr = e.iterator();
		int i = 0;
		while(itr.hasNext()) {
			Object key = itr.next();
			try {
				crits[i][0] = ((DatRulesCriterion)key).createCopy();
				Vector v = (Vector)all.get(key);
				DatRulesCriterion[] line = new DatRulesCriterion[v.size()];
				for(int j = 0; j < v.size(); j++) {
					line[j] = (DatRulesCriterion)v.elementAt(j);
				}
				((DatRulesCriterion)crits[i][0]).m_tooltipText = ((DatRulesCriterion)crits[i][0]).m_tooltipSugText;
				crits[i][1] = line;
				i++;
			} catch(Exception e1) {

			}
		}

		return crits;
	}

	public static DatRulesOperator getOperatorByWorkText(String text, DatRulesOperator[] operations)
	{
		for(int i = 0; i < operations.length; i++) {
			DatRulesOperator crit = operations[i];
			if(crit.getWorkText().equals(text)) {
				return crit;
			}
		}
		return null;
	}
        
	public static DatRulesMethod getMethodByWorkText(String text, DatRulesMethod[] operations)
	{
		for(int i = 0; i < operations.length; i++) {
			DatRulesMethod crit = operations[i];
			if(crit.getWorkText().equals(text)) {
				return crit;
			}
		}
		return null;
	}
        

	public static DatRulesOperator[] getLinkedOperator()
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_opListLinks sollte die Methode synchronized sein. */
		if(m_opListLinks == null) {
			m_opListLinks = new DatRulesOperator[2];
			m_opListLinks[0] = m_opList[1];
			m_opListLinks[1] = m_opList[2];
		}
		return m_opListLinks;
	}

	public static DatRulesOperator[] getValueOperator()
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_opListVal sollte die Methode synchronized sein. */
		if(m_opListVal == null) {
			Vector lst = new Vector();
			for(int i = 0; i < m_opList.length; i++) {
				if(!m_opList[i].m_displayText.equals("und")
					&& !m_opList[i].m_displayText.equals("oder")) {
					lst.add(m_opList[i]);
				}
			}
			m_opListVal = new DatRulesOperator[lst.size()];
			for(int i = 0; i < lst.size(); i++) {
				m_opListVal[i] = (DatRulesOperator)lst.get(i);
			}

		}
		return m_opListVal;
	}

	/**
	 *  Returns the array of operations which are possible for the criterium krit
	 * @param krit String
	 * @return DatRulesOperator[]
	 */
	public static DatRulesOperator[] getValueOperator(String krit)
	{
		DatRulesOperator[] ret = null;
		return ret;
	}

	public static DatRulesAction getActionByWorkText(String text)
	{
		for(int i = 0; i < m_actList.length; i++) {
			DatRulesAction crit = m_actList[i];
			if(crit.m_workText.equals(text)) {
				return crit;
			}
		}
		return m_actList[m_actList.length - 1];
	}

	public static DatRulesAction getActionByWorkID(int aid)
	{
		for(int i = 0; i < m_actList.length; i++) {
			DatRulesAction crit = m_actList[i];
			if(crit.m_id == aid) {
				return crit;
			}
		}
		return null;
	}

	public static String getTypKeyString(String rulesType)
	{
		if(rulesType.equals(DatCaseRuleAttributes.RULES_TYPE_ERROR)) {
			return "Fehler";
		} else if(rulesType.equals(DatCaseRuleAttributes.RULES_TYPE_WARNING)) {
			return "Warnung";
		} else if(rulesType.equals(DatCaseRuleAttributes.RULES_TYPE_SUGGESTION)) {
			return "Hinweis";
		} else {
			return "";
		}
	}

	public static String getXMLText(String text)
	{
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		return text.replaceAll("\n", "&#013");
	}

	public static String getDisplayText(String text)
	{
		text = text.replaceAll("&lt;", "<");
		text = text.replaceAll("&gt;", ">");
		if(text.length() >= 2) {
			if((text.charAt(0) == '>' || text.charAt(0) == '<')
				&& text.charAt(1) == '&') {
				text = text.replaceAll("&", "");
			}
		}
		return text.replaceAll("&#013", "\n");
	}

	public static String convertToAscii(String text)
	{
		try {
			text = text.replaceAll("&lt;", "<");
			text = text.replaceAll("&gt;", ">");
			/*if (text.equals("&lt;="))
				  return "<=";
			 else if (text.equals("&gt;="))
				  return ">=";
			 else*/
			return text;
		} catch(Exception ex) {
			return text;
		}
	}

	public static String getFormatedCodeText(String val,
		DatRulesCriterion kritObj)
	{
		if(kritObj.m_dataType == null)
			return "";
		if(kritObj.m_dataType.equals(String.class)) {
			String chk = "";
			for(int i = 0; i < val.length(); i++) {
				if(i > 0 && i < val.length() - 2
					&& val.charAt(i) == '\''
					&& (val.charAt(i + 1) != '\''
					|| val.charAt(i - 1) != '\'')) {
					chk = chk + val.charAt(i) + "\'";
				} else {
					if(val.charAt(i) == '*') {
						chk = chk + '%';
					} else {
						chk = chk + val.charAt(i);
					}
				}
			}
			val = chk.toUpperCase();
			/*try{
			 val = chk.replaceAll("*", "%");
			   }catch (Exception ex){}*/
			if(val.length() > 0) {
				if(val.charAt(0) != '\'') {
					val = "'" + val;
				}
				if(val.charAt(val.length() - 1) != '\'') {
					val = val + "'";
				}
			}
		} else if(kritObj.m_dataType.equals(Double.class)) {
			val = val.replaceAll(",", ".");
		}

		return val;

	}

	public static long[] getRolesFromElement(Element ele)
	{
		return 	geValuesFromElement(ele, DatCaseRuleAttributes.ATT_CAPTION_ROLE);
	}

	public static long[] geValuesFromElement(Element ele, String attribute)
	{
		String attr = ele.getAttribute(attribute);
			if(attr != null && attr.trim().length() > 0) {
				return CommonOperations.getLongArrayFromString(attr, ",");
			}
		return null;

	}

	public static boolean isForRole(long roleID, long[] roles)
	{
		if(roles == null) {
			return true;
		} else {
			for(int i = 0; i < roles.length; i++) {
				if((roles[i] == roleID)/* || (roles[i] == 0)*/) {
					return true;
				}
			}
			return false;
		}
	}

	public static boolean isForRole(Element ele, long roleID)
	{
		long[] roles = getRolesFromElement(ele);
		return isForRole(roleID, roles);
	}

}
