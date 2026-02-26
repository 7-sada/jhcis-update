/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcu.PersistentClass;

import com.mysql.jdbc.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Scud
 */
public class DBPersistentRequestObject {

    static String date;
    protected static String request;
    Statement stat;

    public String getRequest() {
        return this.request;
    }

    private final static String[] filterField = {
        "visitdiag",
        "visitdrug",
        "visitepi",
        "personchronic",
        "f43specialpp",
        "person",
        "visit",
        "visitancpregnancy",
        "visitanc",
        "visitancdeliver",
        "visitancmothercare"
    };
////////////////////////////////////////////////////////////////////////////////    
////scud 20160913 //////////////////////////////////////////////////////////////

    public static boolean checkDeletePerson(String sql) {
        Pattern checkSeparator = Pattern.compile("DELETE FROM person.*");
        Matcher matcher = checkSeparator.matcher(sql);
        return matcher.find();
    }

    public static String getPidPerson(String sql) {
        String[] word = sql.split(" ");
        for (int i = 0; i < word.length; i++) {
            if (word[i].equals("pid")) {
                return word[i + 2];
            }
        }
        return "";
    }

    public static String getPcucodePerson(String sql) {
        String[] word = sql.split(" ");
        for (int i = 0; i < word.length; i++) {
            if (word[i].equals("pcucodeperson")) {
                return word[i + 1].replace("=", "");
            }
        }
        return "";
    }

    public static String getSQLDeletePerson(String pid, String PcucodePerson, String PatientPid) {
        return "INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\"person\""
                + ",\"{\\\"PID\\\":\\\""
                + pid
                + "\\\",\\\"HOSPCODE\\\":\\\""
                + PcucodePerson
                + "\\\",\\\"EDITOR_CID\\\":\\\""
                + UserInfo.user_cid + "\\\""
                + ",\\\"PID\\\":\\\""
                + PatientPid + "\\\""
                + "}\",\""
                + UserInfo.username + "\")";
    }
////////////////////////////////////////////////////////////////////////////////

    private static boolean checkTemporaryOffline(String sqlSubmit) {
        Pattern deleteTemporaryOffline = Pattern.compile("delete from _tmpoffline.*");
        Matcher checkOffline = deleteTemporaryOffline.matcher(sqlSubmit);
        return checkOffline.find();
    }

    private static boolean checkAsciiDefaultCode(String sqlSubmit) {
        Pattern deleteTemporaryOffline = Pattern.compile(".*<>ASCII.*");
        Matcher checkOffline = deleteTemporaryOffline.matcher(sqlSubmit);
        return checkOffline.find();
    }
//
//    
//    

    public static String replicateSQLUpdateDeleteFilter(String sql, String pidPatient, String dateServ, Statement stat) {

        //System.out.println("69:sql /// replicateSQLUpdateDeleteFilter : " + sql);
        if (checkTemporaryOffline(sql) || checkAsciiDefaultCode(sql)) {
            return "";
        }
        //
        //DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        if (sql.split(" ")[0].toLowerCase().equals("update")
                && Arrays.asList(filterField).contains(clearSpecialCharacter(sql.split(" ")[1].toLowerCase()))) {
            System.out.println("UPDATE Case 1 : " + sql);
            return sqlTransform(sql, sql.split(" ")[0].toLowerCase(), clearSpecialCharacter(sql.split(" ")[1].toLowerCase()), pidPatient, dateServ, stat);
        } else if (sql.split(" ")[0].toLowerCase().equals("delete")
                && (Arrays.asList(filterField).contains(clearSpecialCharacter(sql.split(" ")[2].toLowerCase())) || Arrays.asList(filterField).contains(clearSpecialCharacter(sql.split(" ")[3].toLowerCase())))) {
            ///Old Statement : Arrays.asList(filterField).contains(sql.split(" ")[2].toLowerCase())
            System.out.println("Delete Case 0 : " + sql);
            if (clearSpecialCharacter(sql.split(" ")[3].toLowerCase()).equals(keyTableFormDeleteSQL(sql))) {
                System.out.println("Delete Case 1 : " + sql);
                return sqlTransform(convertDeleteSQLPattern2to1(sql, keyTableFormDeleteSQL(sql)), sql.split(" ")[0].toLowerCase(), keyTableFormDeleteSQL(sql), pidPatient, dateServ, stat);
            } else {
                System.out.println("Delete Case 2 : " + sql);
                return sqlTransform(sql, sql.split(" ")[0].toLowerCase(), keyTableFormDeleteSQL(sql), pidPatient, dateServ, stat);
            }
        }
        return "";
    }

    private static String convertDeleteSQLPattern2to1(String sql, String sqlType) {
        //System.out.println("This is Type 2!!");
        return "DELETE FROM " + sqlType + " WHERE diagcode= '" + searchPatternCode(sql.split("and"), "visitdiag.diagcode") + "' AND pcucode= '" + searchPatternCode(sql.split("and"), "visitdiag.pcucode") + "' AND pid= '" + searchPatternCode(sql.split("and"), "visit.pid") + "'";
    }

    private static String searchPatternCode(String[] checkPattern, String codetype) {
        //When check pcucode / code type = "visitdiag.pcucode" NOT "pcucode"
        //System.out.println("Codetype : " + codetype);
        Pattern checkCode = Pattern.compile(".*" + codetype + ".*");
        Matcher matcher;
        for (String pattern : checkPattern) {
            String p = pattern.replace(" ", "");
            //System.out.println(p);
            matcher = checkCode.matcher(p);
            if (matcher.find()) {
                //System.out.println("FOUND!! : " + p.split("'")[1].replace("\\",""));
                return p.split("'")[1].replace("\\", "");
            }
        }
        return " ";
    }

    private static String clearSpecialCharacter(String string) {
        return string.replaceAll("[-+.^:,]", "");
    }

    private static String keyTableFormDeleteSQL(String sql) {
        if (Arrays.asList(filterField).contains(clearSpecialCharacter(sql.split(" ")[2].toLowerCase()))) {
            return sql.split(" ")[2].toLowerCase();
        } else {
            return clearSpecialCharacter(sql.split(" ")[3].toLowerCase());
        }
    }

    private static String sqlTransform(String sql, String sqlCommand, String sqlType, String patientPID, String dateServ, Statement stat) {

        //System.out.println("SQL Transform Activated : " + sql);

        switch (sqlType.toLowerCase()) {
            case "visitepi": {
                //System.out.println("Epi Activated : " + sql);
                //System.out.println("Before Convert : " + sql);
                sql = visitepiConvert(sql);
                sql = getConvertWordSQLformVisitEpi(sql.split(" "));
                //System.out.println("After Convert : " + sql);

            }
            break;
            case "personchronic": {
                //System.out.println("Chronic Process...");
                sql = chronicConvert(sql);
                //System.out.println("Chronic SQL Convert : " + sql);
                break;
            }
            case "f43specialpp": {
                sql = f43specialpConvert(sql);
                break;
            }
            case "visitdrug": {
                sql = convertDrug24(sql);
                //System.out.println("SQL After Convert 24 : " + sql);
                if (sqlCommand.toLowerCase().equals("update")) {
                    sql = visitdrugConvert(sql);
                }
                sql = getConvertWordSQLformVisitDrug(sql.split(" "));
                break;
            }
            case "visitdiag": {
                if (sqlCommand.toLowerCase().equals("update")) {
                    sql = visitdiagConvert(sql);
                }
                sql = getConvertWordSQLformVisitDiag(sql.split(" "));
                break;
            }
            case "visit": {
                sql = visitConvert(sql);
                break;
            }
            case "person": {
                sql = personConvert(sql);
                break;
            }
            case "visitancpregnancy": {
                sql = visitAncPregnancyConvert(sql);
                break;
            }
            case "visitanc": {
                sql = visitAncConvert(sql);
                break;
            }
            case "visitancdeliver": {
                sql = visitAncDeliverConvert(sql);
                break;
            }
            case "visitancmothercare": {
                sql = visitAncMotherCareConvert(sql);
                break;
            }
            default:
                break;
        }

        /*
        if (sqlType.toLowerCase().equals("visitepi")) {
            //System.out.println("Epi Activated : " + sql);
            //System.out.println("Before Convert : " + sql);
            sql = visitepiConvert(sql);
            //System.out.println("After Convert : " + sql);
        }*/
        //System.out.println("sqltype = " + sqlType);
        /*if (sqlType.toLowerCase().equals("personchronic")) {
            //System.out.println("Chronic Process...");
            sql = chronicConvert(sql);
            //System.out.println("Chronic SQL Convert : " + sql);
        }*/

 /*
        if (sqlType.toLowerCase().equals("f43specialpp")) {
            sql = f43specialpConvert(sql);
        }*/
        //if (sqlType.toLowerCase().equals("visitdrug") && sqlCommand.toLowerCase().equals("update")) {
        /*
        if (sqlType.toLowerCase().equals("visitdrug")) {

            sql = convertDrug24(sql);
            //System.out.println("SQL After Convert 24 : " + sql);
            if (sqlCommand.toLowerCase().equals("update")) {
                sql = visitdrugConvert(sql);
            }
        }
         */
 /*
        if (sqlType.toLowerCase().equals("visitdiag") && sqlCommand.toLowerCase().equals("update")) {
            sql = visitdiagConvert(sql);
        }*/
        //System.out.println("Convert Name : " + sql);
        return generateInsertSql(convertName(sql.split(" ")), matchName(sqlType), patientPID, dateServ, sqlCommand);
    }

    private static String f43specialpConvert(String sql) {
        sql = sql.replace("f43specialpp", "specialpp");
        sql = sql.replace("pcucodeperson", "HOSPCODE");
        sql = sql.replace("pid", "PID");
        sql = sql.replace("visitno", "SEQ");
        sql = sql.replace("servplace", "SERVPLACE");
        sql = sql.replace("provider", "PROVIDER");
        sql = sql.replace("ppspecial", "PPSPACIAL");
        sql = sql.replace("ppsplace", "PPSPLACE");
        sql = sql.replace("dateserv", "DATE_SERV");
        return sql;
    }

    private static String chronicConvert(String sql) {

        sql = sql.replace("pcucodeperson", "HOSPCODE");
        sql = sql.replace("pid", "PID");
        sql = sql.replace("chroniccode", "CHRONIC");

        sql = sql.replace("provider", "PROVIDER");
        sql = sql.replace("ppsplace", "PPSPLACE");

        return sql;
    }
    
    ////////////////////// Edit me Please!!!! //////////////////////////
    
    private static String visitConvert(String sql)
    {
        sql = sql.replace("f43specialpp", "specialpp");
        return sql;
    }
    
    private static String personConvert(String sql)
    {
        //////////////////////
        return sql;
    }
    
    
    private static String visitAncPregnancyConvert(String sql)
    {
        sql = sql.replace("dateserv", "DATE_SERV");
        sql = sql.replace("gravida", "GRAVIDA");
        return sql;
    }
    
    private static String visitAncConvert(String sql)
    {
        sql = sql.replace("dateserv", "DATE_SERV");
        sql = sql.replace("gravida", "GRAVIDA");
        return sql;
    }
    
    //prenatal
    private static String visitAncDeliverConvert(String sql)
    {
        sql = sql.replace("dateserv", "DATE_SERV");
        sql = sql.replace("gravida", "GRAVIDA");
        return sql;
    }
    
    //postnatal
    private static String visitAncMotherCareConvert(String sql)
    {
        sql = sql.replace("dateserv", "DATE_SERV");
        sql = sql.replace("gravida", "GRAVIDA");
        return sql;
    }

    private static String visitdrugConvert(String sql) {

        boolean delTrigger = false;

        Pattern patternCostPrice = Pattern.compile("set.*costprice.*where");
        Pattern patternRealPrice = Pattern.compile("set.*realprice.*where");
        Pattern patternUnit = Pattern.compile("set.*unit.*where");
        Pattern patterndateUpdate = Pattern.compile("set.*dateupdate.*=.*now().*where");
        Pattern patternDrugCode = Pattern.compile("set.*drugcode.*where");

        Matcher matcher1 = patternCostPrice.matcher(sql.toLowerCase());
        Matcher matcher2 = patternRealPrice.matcher(sql.toLowerCase());
        Matcher matcher3 = patternUnit.matcher(sql.toLowerCase());
        Matcher matcher4 = patterndateUpdate.matcher(sql.toLowerCase());
        Matcher matcher5 = patternDrugCode.matcher(sql.toLowerCase());

        try {

            if (matcher1.find() || matcher2.find() || matcher3.find() || matcher5.find()) {
                String[] temp = sql.split(" ");

                for (int i = 0; i < temp.length; i++) {

                    if (temp[i].toLowerCase().equals("where")) {
                        break;
                    }

                    if (temp[i].toLowerCase().equals("set")) {
                        delTrigger = true;
                        continue;
                    }

                    if (delTrigger) {
                        if (checkPreservePattern(temp[i])) {
                            if (temp[i].length() > 0 && temp[i].charAt(temp[i].length() - 1) == ',') {
                                //System.out.println("Confirm Original preserve string Before : " + temp[i]);
                                temp[i] = temp[i].substring(0, temp[i].length() - 1);
                                //System.out.println("Confirm Original preserve string After : " + temp[i]);
                            }
                        } else {
                            temp[i] = "";
                        }
                    }
                }
                return joinString(" ", temp);
            }
            return "";
        } catch (Exception e) {
            System.err.println("Visit Drug Error Please Check SQL : " + sql);
            return "";
        }
    }

    private static String visitdiagConvert(String sql) {
        Pattern patternDiag = Pattern.compile("set.*diagcode.*where");
        Pattern patternDate = Pattern.compile("set.*dateupdate.*where");

        Matcher matcher = patternDiag.matcher(sql.toLowerCase());
        Matcher matcher2 = patternDate.matcher(sql.toLowerCase());

        //Pattern patternDiagMid = Pattern.compile("diagcode.*");
        /* Pattern patternD = Pattern.compile("dateupdate");
         Pattern patternE = Pattern.compile("=");
         Pattern patternN = Pattern.compile("now()");*/
        Matcher D, E, N;

        boolean delTrigger = false;

        if (matcher.find()) {
            String[] temp = sql.split(" ");

            for (int i = 0; i < temp.length; i++) {

                if (temp[i].toLowerCase().equals("where")) {
                    break;
                }

                if (temp[i].toLowerCase().equals("set")) {
                    delTrigger = true;
                    continue;
                }

                if (delTrigger) {
                    // matcher = patternDiagMid.matcher(temp[i].toLowerCase());

                    if (!checkPreservePattern(temp[i].toLowerCase())) {
                        temp[i] = "";
                    } else if (temp[i].length() > 0 && temp[i].charAt(temp[i].length() - 1) == ',') {
                        temp[i] = temp[i].substring(0, temp[i].length() - 1);
                    }
                }
            }
            return joinString(" ", temp);
        }
        return "";
    }

    private static String visitepiConvert(String sql) {
        Pattern patternVaccineCode = Pattern.compile("vaccinecode=");
        Pattern delFlagEpi = Pattern.compile("flag18fileexpo=");
        Pattern dateUpdate = Pattern.compile("dateupdate.*");
        Matcher matcherPcu, matcherDelFlag, matcherDate;
        matcherDate = dateUpdate.matcher(sql.toLowerCase());
        //System.out.println("Epi Pattern Activate : " + sql);

        String[] arr = sql.split(" ");
        for (int i = 0; i < arr.length; i++) {
            matcherPcu = patternVaccineCode.matcher(arr[i].toLowerCase());

            if (matcherPcu.find()) {
                if (matcherPcu.matches()) {
                    //System.out.println("Vaccine get : " + arr[i+1]);
                    arr[i + 1] = "\\'" + VaccineMappingCode.getVaccineMappingCode().get(arr[i + 1].replace("\\'", "")) + "\\'";
                } else {
                    //System.out.println("Vaccine get : " + arr[i]);
                    arr[i] = "vaccinecode=\\'" + VaccineMappingCode.getVaccineMappingCode().get(arr[i].replace("vaccinecode=\\'", "").replace("\\'\\,", "").replace("\\'", "")) + "\\'";
                }
            }
            /*    boolean specialCase = false;
            
            if (matcherPcu.find()) {

                try {

                    matcherDelFlag = delFlagEpi.matcher(arr[i + 1]);

                    if (arr[i + 1].equals("=")) {
                        i = i + 1;
                    }

                } catch (Exception e) {

                    if (matcherDate.find()) {
                        specialCase = true;
                    }
                    matcherDelFlag = delFlagEpi.matcher(arr[i]);
                }

                if (matcherDelFlag.find() || specialCase) {
                    try {
                        if (!specialCase) {
                            arr[i + 1] = "";
                        }
                        System.out.println("Mapping Key 1 : " + arr[i].replace("vaccinecode=\\'", "").replace("\\'\\,", ""));
                        //arr[i] = "vaccinecode=\\'" + VaccineMappingCode.VaccineMappingCode.get(arr[i].replace("vaccinecode=\\'", "").replace("\\'\\,","").replace("\\'", "")) + "\\'";
                        System.out.println("Hash Key : " + arr[i].replace("vaccinecode=\\'", "").replace("\\'\\,", "").replace("\\'", ""));
                        arr[i] = "vaccinecode=\\'" + VaccineMappingCode.getVaccineMappingCode().get(arr[i].replace("vaccinecode=\\'", "").replace("\\'\\,", "").replace("\\'", "")) + "\\'";
                    } catch (Exception e) {
                        //arr[i] = "vaccinecode=\\'" + "Nodata" + "\\'";
                        arr[i] = null;
                    }
                } else {
                    try {
                        System.out.println("Mapping Key 2 : " + arr[i + 1].replace("\\'", ""));
                        //arr[i + 1] = "\\'" + VaccineMappingCode.VaccineMappingCode.get(arr[i + 1].replace("\\'", "")) + "\\'";
                        arr[i + 1] = "\\'" + VaccineMappingCode.getVaccineMappingCode().get(arr[i + 1].replace("\\'", "")) + "\\'";
                    } catch (Exception e) {
                        //arr[i + 1] = "\\'" + "Nodata" + "\\'";
                        arr[i + 1] = null;
                    }
                }

            }*/
        }

        sql = joinString(" ", arr);

        //////////////////////////////////////////
        Pattern delPcuCodePerson = Pattern.compile("where.*pcucodeperson.*and");
        Matcher delpcuperson = delPcuCodePerson.matcher(sql.toLowerCase());
        Pattern pcuCodePersonMid = Pattern.compile("pcucodeperson.*");
        Matcher checkMid;

        if (delpcuperson.find()) {
            String[] element = sql.split(" ");
            boolean cutTrigger = false;
            boolean checkAndMid = false;

            for (int i = 0; i < element.length; i++) {
                checkMid = pcuCodePersonMid.matcher(element[i]);

                if (element[i].toLowerCase().equals("and") && checkAndMid) {
                    element[i] = "";
                    cutTrigger = false;
                    break;
                }

                if (cutTrigger) {
                    element[i] = "";
                }

                if (checkMid.find()) {
                    cutTrigger = true;
                    checkAndMid = true;
                    element[i] = "";
                }

            }

            sql = joinString(" ", element);
        }

        //////////////////////////////////////////
        boolean delTrigger = false;

        Pattern patternPcu = Pattern.compile("set.*pcucode.*where");
        Pattern patternVisit = Pattern.compile("set.*visitno.*where");
        Pattern patternhosservice = Pattern.compile("set.*hosservice.*where");
        Pattern patterndateUpdate = Pattern.compile("set.*dateupdate.*where");
        Pattern patternVaccine = Pattern.compile("set.*vaccinecode.*where");
        Pattern patternDelete = Pattern.compile("delete.*from.*");

        Matcher matcher1 = patternPcu.matcher(sql.toLowerCase());
        Matcher matcher2 = patternVisit.matcher(sql.toLowerCase());
        Matcher matcher3 = patternhosservice.matcher(sql.toLowerCase());
        Matcher matcher4 = patterndateUpdate.matcher(sql.toLowerCase());
        Matcher matcher5 = patternVaccine.matcher(sql.toLowerCase());
        Matcher delMatcher = patternDelete.matcher(sql.toLowerCase());

        if (delMatcher.find()) {
            return sql;
        }

        if (matcher1.find() || matcher2.find() || matcher3.find() || matcher5.find()) {
            String[] temp = sql.split(" ");

            for (int i = 0; i < temp.length; i++) {

                if (temp[i].toLowerCase().equals("where")) {
                    break;
                }

                if (temp[i].toLowerCase().equals("set")) {
                    delTrigger = true;
                    continue;
                }

                if (delTrigger) {
                    if (checkPreservePattern(temp[i])) {
                        if (temp[i].length() > 0 && temp[i].charAt(temp[i].length() - 1) == ',') {
                            //System.out.println("Confirm Original preserve string Before : " + temp[i]);
                            temp[i] = temp[i].substring(0, temp[i].length() - 1);
                            //System.out.println("Confirm Original preserve string After : " + temp[i]);
                        }
                    } else {
                        temp[i] = "";
                    }
                }
            }
            return joinString(" ", temp);
        }
        return "";

        ////////////////////////////////////
        /*System.out.println("Final Result form VisitEpi Convert : " + joinString(" ", arr));
         return joinString(" ", arr);*/
    }

    private static boolean checkPreservePattern(String string) {
        Matcher matcher;
        Pattern patternPcuMid = Pattern.compile("pcucode.*");
        Pattern patternVisitMid = Pattern.compile("visitno.*");
        Pattern patternHosserviceMid = Pattern.compile("hosservice.*");
        Pattern patternDateUpdate = Pattern.compile("dateupdate.*");
        Pattern patternEqual = Pattern.compile("=");
        Pattern patternNow = Pattern.compile("now()");
        Pattern patternVaccineCode = Pattern.compile("vaccinecode.*");
        Pattern patternUnit = Pattern.compile("unit.*");
        Pattern patternCostPrice = Pattern.compile("costprice.*");
        Pattern patternRealPrice = Pattern.compile("realprice.*");
        Pattern patternDrugCode = Pattern.compile("drugcode.*");
        Pattern patternDiagCode = Pattern.compile("diagcode.*");

        //System.out.println("Check Pattern : " + string);
        matcher = patternDiagCode.matcher(string.toLowerCase());
        if (matcher.find()) {
            //System.out.println("Found preserve String pcu : " + string);
            return true;
        }

        matcher = patternEqual.matcher(string.toLowerCase());
        if (matcher.matches()) {
            //System.out.println("Found preserve String pcu : " + string);
            return true;
        }

        matcher = patternNow.matcher(string.toLowerCase());
        if (matcher.find()) {
            //System.out.println("Found preserve String pcu : " + string);
            return true;
        }

        matcher = patternDrugCode.matcher(string.toLowerCase());
        if (matcher.find()) {
            //System.out.println("Found preserve String pcu : " + string);
            return true;
        }

        matcher = patternUnit.matcher(string.toLowerCase());
        if (matcher.find()) {
            //System.out.println("Found preserve String pcu : " + string);
            return true;
        }

        matcher = patternCostPrice.matcher(string.toLowerCase());
        if (matcher.find()) {
            //System.out.println("Found preserve String pcu : " + string);
            return true;
        }

        matcher = patternRealPrice.matcher(string.toLowerCase());
        if (matcher.find()) {
            //System.out.println("Found preserve String pcu : " + string);
            return true;
        }

        matcher = patternVaccineCode.matcher(string.toLowerCase());
        if (matcher.find()) {
            //System.out.println("Found preserve String pcu : " + string);
            return true;
        }

        matcher = patternDateUpdate.matcher(string.toLowerCase());
        if (matcher.find()) {
            //System.out.println("Found preserve String pcu : " + string);
            return true;
        }

        matcher = patternPcuMid.matcher(string.toLowerCase());
        if (matcher.find()) {
            //System.out.println("Found preserve String pcu : " + string);
            return true;
        }

        matcher = patternVisitMid.matcher(string.toLowerCase());
        if (matcher.find()) {
            //System.out.println("Found preserve String visit : " + string);
            return true;
        }

        matcher = patternHosserviceMid.matcher(string.toLowerCase());
        if (matcher.find()) {
            //System.out.println("Found preserve String hosservice : " + string);
            return true;
        }

        return false;
    }

    public static String getPidFormSQL(String sql) {
        try {
            String[] sqlArrayPattern = sql.split(" ");
            Pattern patternPID = Pattern.compile("pid.*");
            Matcher matchPid;
            for (int i = 0; i < sqlArrayPattern.length; i++) {
                matchPid = patternPID.matcher(sqlArrayPattern[i]);
                if (matchPid.find()) {
                    //System.out.println("epiArray[i+1] : " + epiArray[i+1]);
                    //System.out.println("FOUND PID!! form SQL : " + sql);
                    return sqlArrayPattern[i + 1].replace("\\", "").replace("'", "").replace("=", "");
                }
            }
        } catch (Exception e) {
            System.err.println("Warning from method getPidFormSQL in DBPersistentRequestObject.java");
            return "";
        }
        //System.out.println("NOT FOUND PID from this SQL : " + sql);
        return "";
    }

    private static String generateInsertSql(String value, String tablename, String PatientPid, String dateServ, String sqlCommand) {

        //System.out.println("SQL Generated Selection for Process : " + value);
        if (value.equals("")) {
            return "";
        }
        /**
         * Config Code.
         */
        //String targetTable = "replicate_log_update_delete_filter";

        String sql="";
        /*
         DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
         Date date = new Date();*/

        //return "INSERT INTO " + targetTable + " (" + targetField1 + "," + targetField2 + ") VALUES ( " + DBPersistentObjectFilter.date + ",\"" + value + "\") ";
        //return "INSERT INTO " + targetTable + " (" + targetField1 + "," + targetField2 + ") VALUES (DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\"" + value + "\") ";
        //////////////////////////////////////////////////////////////////////
        switch (sqlCommand.toLowerCase()) {
            //////////////////// DELETE ZONE ///////////////////
            case "delete": {
                switch (tablename) {
                    case "chronic": {
                        sql = "INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\"" + tablename
                                + "\",\"{" + getJSONHashmapFormatForChronic(value)
                                + ",\\\"EDITOR_CID\\\":\\\""
                                + UserInfo.user_cid + "\\\"}\",\""
                                + UserInfo.username + "\")";
                        break;
                    }
                    case "epi": {
                        System.out.println("Generated SQL Delete epi..." + value);
                        sql = "INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\"" + tablename
                                + "\",\"{" + getJSONHashmapFormat(value)
                                + ",\\\"EDITOR_CID\\\":\\\""
                                + UserInfo.user_cid
                                + "\\\""
                                + ",\\\"HOSPCODE\\\":\\\""
                                + UserInfo.pcucode
                                + "\\\"}\",\""
                                + UserInfo.username + "\")";
                        break;
                    }
                    default: {
                        if (dateServ.equals("")) {
                            System.out.println("Generated Request_Delete SQL Command....");
                            sql = "INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\"" + tablename
                                    + "\",\"{" + getJSONHashmapFormat(value)
                                    + ",\\\"EDITOR_CID\\\":\\\""
                                    + UserInfo.user_cid
                                    + "\\\",\\\"PID\\\":\\\""
                                    + PatientPid
                                    + "\\\"}\",\""
                                    + UserInfo.username + "\")";
                            System.out.println("SQL request_delete Line 615 No Date Serve Data : " + sql);
                        } else {
                            sql = "INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\"" + tablename
                                    + "\",\"{" + getJSONHashmapFormat(value)
                                    + ",\\\"EDITOR_CID\\\":\\\""
                                    + UserInfo.user_cid
                                    + "\\\",\\\"PID\\\":\\\""
                                    + PatientPid
                                    + "\\\",\\\"DATE_SERV\\\":\\\""
                                    + dateServ + "\\\"}\",\""
                                    + UserInfo.username + "\")";
                            System.out.println("SQL request_delete Line 571 : " + sql);
                            
                            
                        }
                        break;
                    }
                }
                break;
            }
            //////////////////// UPDATE ZONE ///////////////////
            case "update": {
                switch (tablename) {
                    case "chronic": {
                        sql = generateUpdateKeyForChronic("chronic", getJSONHashmapFormatForChronicUpdate(value), value);
                        break;
                    }
                    default: {
                        sql = generateUpdateKey(value.split(" ")[1], getJSONHashmapFormat(value), value, PatientPid, dateServ);
                        }
                }
            }
            default: {
                System.out.println("Not delete or Update Please Check it again");
                break;
            }
        }

        //////////////////////////////////////////////////////////////////////
        
        /*
        if (value.split(" ")[0].toLowerCase().equals("delete")) {

            /////////// REQUEST DELETE ZONE ///////////
            //System.out.println("Deleted Passed!!!!!!!");
            //System.out.println("Check table = " + value.split(" ")[2]);
            //System.out.println("Check JSON Value = " + getJSONHashmapFormat(value));     
            if (tablename.equals("chronic")) {
                sql = "INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\"" + tablename
                        + "\",\"{" + getJSONHashmapFormatForChronic(value)
                        + ",\\\"EDITOR_CID\\\":\\\""
                        + UserInfo.user_cid + "\\\"}\",\""
                        + UserInfo.username + "\")";
            } else if (tablename.equals("epi")) {
                System.out.println("Generated SQL Delete epi..." + value);
                sql = "INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\"" + tablename
                        + "\",\"{" + getJSONHashmapFormat(value)
                        + ",\\\"EDITOR_CID\\\":\\\""
                        + UserInfo.user_cid
                        + "\\\""
                        + ",\\\"HOSPCODE\\\":\\\""
                        + UserInfo.pcucode
                        + "\\\"}\",\""
                        + UserInfo.username + "\")";
            } else if (dateServ.equals("") && !tablename.equals("chronic")) {  //ถ้าไม่มี Date Serve การสร้าง SQL Command Insert into ไม่ต้องมีช่อง
                System.out.println("Generated Request_Delete SQL Command....");
                sql = "INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\"" + tablename
                        + "\",\"{" + getJSONHashmapFormat(value)
                        + ",\\\"EDITOR_CID\\\":\\\""
                        + UserInfo.user_cid
                        + "\\\",\\\"PID\\\":\\\""
                        + PatientPid
                        + "\\\"}\",\""
                        + UserInfo.username + "\")";
                System.out.println("SQL request_delete Line 615 No Date Serve Data : " + sql);
            } else {
                sql = "INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\"" + tablename
                        + "\",\"{" + getJSONHashmapFormat(value)
                        + ",\\\"EDITOR_CID\\\":\\\""
                        + UserInfo.user_cid
                        + "\\\",\\\"PID\\\":\\\""
                        + PatientPid
                        + "\\\",\\\"DATE_SERV\\\":\\\""
                        + dateServ + "\\\"}\",\""
                        + UserInfo.username + "\")";
                System.out.println("SQL request_delete Line 571 : " + sql);
            }
        } else /////////// REQUEST UPDATE ZONE ///////////
        //System.out.println("UPDATE Passed!!!!!!!");
        //System.out.println("Generated Request_UPDATE SQL Command....");
        {
            if (tablename.equals("chronic")) {
                //System.out.println("Generated String for Chronic....");
                sql = generateUpdateKeyForChronic("chronic", getJSONHashmapFormatForChronicUpdate(value), value);
            } else {
                //System.out.println("SQL Before generate : " + value);
                sql = generateUpdateKey(value.split(" ")[1], getJSONHashmapFormat(value), value, PatientPid, dateServ);
                //System.out.println("Result SQL : " + sql);
            }//sql = "INSERT INTO request_update (dateupdate,tablename,pk_values,values_set) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\"" + value.split(" ")[1] + "\",\"{" + getJSONHashmapFormat(value) + "}\",\"{" + getUpdateString(value) + "}\")";        //System.out.println("Result SQL : " + sql);        /// Check DiagCode more than two for set Format new JSON String Array.
        }
        */
        
        if (tablename.equals("diagnosis_opd")) {

            //System.out.println("SQL Diagnosis Test : " + sql);
            Pattern diagcodeMoreThan1 = Pattern.compile(".*DIAGCODE.*DIAGCODE.*");
            //Pattern diagcodeConditionWhere = Pattern.compile(".*DIAGCODE.*WHERE.*DIAGCODE.*");

            Matcher matcherWhere = diagcodeMoreThan1.matcher(sql);
            //Matcher matcherExceptCondition = diagcodeConditionWhere.matcher(sql);
            //Check Condition Multiply Diagnosis mnore than 1
            //Example : INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),"diagnosis_opd","{\"HOSPCODE\":\"77563\",\"SEQ\":\"221601\",""[{\"DIAGCODE\":\"Z24.6""},{\"DIAGCODE\":\"Z27.1""}],\"EDITOR_CID\":\"4567887778745\",\"PID\":\"790\",\"DATE_SERV\":\"20180201\"}","ss")
            if (matcherWhere.find()) {
                Pattern checkUpdate = Pattern.compile(".*request_update.*");
                Matcher chkUpdate = checkUpdate.matcher(sql);

                if (chkUpdate.find()) {
                    String[] sqlPattern = sql.split(";");
                    sql = sqlPattern[0] + ";" + getMultiplyDiagJSONFormat(sqlPattern[1]);
                } else {
                    sql = getMultiplyDiagJSONFormat(sql);
                }
            }
        }
        return sql;
    }

    private static final Pattern updateEPIPattern = Pattern.compile("VACCINETYPE.*");

    private static String generateUpdateKeyForChronic(String tablename, String updateChronicTarget, String sqlFullString) {
        String sqlGen = "INSERT INTO request_update (dateupdate,tablename,pk_values,values_set,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\""
                + tablename + "\",\"{"
                + updateChronicTarget
                + ",\\\"EDITOR_CID\\\":\\\""
                + UserInfo.user_cid + "\\\"}\",\"{"
                + getChronicJSONUpdateValue(sqlFullString) + "}\",\""
                + UserInfo.username + "\")";
        sqlGen = sqlGen + ";INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\""
                + tablename + "\",\"{"
                + updateChronicTarget + ",\\\"EDITOR_CID\\\":\\\""
                + UserInfo.user_cid
                + "\\\"}\",\""
                + UserInfo.username
                + "\")";

        if (getChronicJSONUpdateValue(sqlFullString).equals("")) {
            //System.out.println("NO Update Value.");
            return "";
        }
        System.out.println("NEW UPDATE SQL [Chronic] : " + sqlGen);
        return sqlGen;
    }

    private static String generateUpdateKey(String tablename, String value, String update, String PatientPid, String dateServ) {
        //Pattern updateEPIPattern = Pattern.compile("VACCINETYPE.*");
        update = getUpdateString(tablename, update);
        //update = getJSONUpdateValue(value);
        String sqlGen;

        //System.out.println("UPDATE Level 2 Passed!!!!!!!");
        //System.out.println("Table Name : " + tablename);
        //System.out.println("Value : " + value);
        //System.out.println("Update : " + update);
        //if (update.equals("") && tablename.equals("epi") && !value.equals("")) {
        if (update.equals("") && tablename.equals("epi") && !value.equals("")) {
            Matcher vaccineType;
            //System.out.println("Value to split : " + value);
            String[] val = value.split(",");
            for (int i = 0; i < filterField.length; i++) {
                vaccineType = updateEPIPattern.matcher(val[i]);
                if (vaccineType.find()) {
                    update = val[i];
                    break;
                }
            }

            sqlGen = "INSERT INTO request_update (dateupdate,tablename,pk_values,values_set,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\""
                    + tablename + "\",\"{"
                    + value
                    + ",\\\"EDITOR_CID\\\":\\\""
                    + UserInfo.user_cid
                    + "\\\""
                    + ",\\\"HOSPCODE\\\":\\\""
                    + UserInfo.pcucode
                    + "\\\"}\",\"{"
                    + update + "}\",\""
                    + UserInfo.username + "\")";
            sqlGen = sqlGen + ";INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\""
                    + tablename + "\",\"{"
                    + value + ",\\\"EDITOR_CID\\\":\\\""
                    + UserInfo.user_cid
                    + "\\\""
                    + ",\\\"HOSPCODE\\\":\\\""
                    + UserInfo.pcucode
                    + "\\\"}\",\""
                    + UserInfo.username
                    + "\")";
            System.out.println("NEW UPDATE SQL [EPI Special] : " + sqlGen);
        } else if (update.equals("")) {
            sqlGen = "";
        } else if (dateServ.equals("")) {
            sqlGen = "INSERT INTO request_update (dateupdate,tablename,pk_values,values_set,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\""
                    + tablename + "\",\"{"
                    + value
                    + ",\\\"EDITOR_CID\\\":\\\""
                    + UserInfo.user_cid
                    + "\\\",\\\"PID\\\":\\\""
                    + PatientPid + "\\"
                    + "}\",\"{"
                    + update + "}\",\""
                    + UserInfo.username
                    + "\")";
            System.out.println("SQL Update prefix [Line 693] = " + sqlGen);

            sqlGen = sqlGen + ";INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\""
                    + tablename + "\",\"{"
                    + value
                    + ",\\\"EDITOR_CID\\\":\\\""
                    + UserInfo.user_cid
                    + "\\\",\\\"PID\\\":\\\""
                    + PatientPid + "\\\"}\",\""
                    + UserInfo.username + "\")";

            System.out.println("Full SQL Add Suffix [Line 702] = " + sqlGen);
        } else {
            sqlGen = "INSERT INTO request_update (dateupdate,tablename,pk_values,values_set,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\""
                    + tablename + "\",\"{"
                    + value
                    + ",\\\"EDITOR_CID\\\":\\\""
                    + UserInfo.user_cid
                    + "\\\",\\\"PID\\\":\\\""
                    + PatientPid + "\\"
                    + "\",\\\"DATE_SERV\\\":\\\""
                    + dateServ + "\\\""
                    + "}\",\"{"
                    + update + "}\",\""
                    + UserInfo.username
                    + "\")";
            //System.out.println("SQL Update prefix [Line 719] = " + sqlGen);
            sqlGen = sqlGen + ";INSERT INTO request_delete (dateupdate,tablename,pk_values,provider) VALUES(DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),\""
                    + tablename + "\",\"{"
                    + value
                    + ",\\\"EDITOR_CID\\\":\\\""
                    + UserInfo.user_cid
                    + "\\\",\\\"PID\\\":\\\""
                    + PatientPid + "\\"
                    + "\",\\\"DATE_SERV\\\":\\\""
                    + dateServ + "\\\""
                    + "}\",\""
                    + UserInfo.username + "\")";
        } //System.out.println("Full SQL Add Suffix [Line 732] = " + sqlGen);
        //System.out.println("NEW UPDATE SQL : " + sqlGen);
        return sqlGen;
    }

    private static String getJSONUpdateValue(String sql) {
        try {
            Pattern wherePattern = Pattern.compile(".*where.*");
            Pattern setPattern = Pattern.compile(".*set.*");
            Matcher matcherWhere = wherePattern.matcher(sql);

            String[] deleteSQLWherePosition;
            if (matcherWhere.find()) {
                deleteSQLWherePosition = sql.split("where");
            } else {
                deleteSQLWherePosition = sql.split("WHERE");
            }

            Matcher matcherAnd = setPattern.matcher(deleteSQLWherePosition[0]);

            String[] attributeArray;
            if (matcherAnd.find()) {
                attributeArray = deleteSQLWherePosition[0].split("set");
            } else {
                attributeArray = deleteSQLWherePosition[0].split("SET");
            }

            //System.out.println("ATTTT : " + attributeArray[1]);
            String[] setUpdate = attributeArray[1].split(",");
            //System.out.println("Attribute String for Check Update : " + attributeArray[1]);
            //String stringForMakeJSONHashmap = deleteSQLWherePosition[1];
            //String[] checkpoint = stringForMakeJSONHashmap.split("AND");
            String JSONFormatValue = "---";
            Pattern dateupdatePattern = Pattern.compile(".*D_UPDATE.*");
            Matcher matcherdate;

            for (int i = 0; i < setUpdate.length; i++) {
                matcherdate = dateupdatePattern.matcher(setUpdate[i]);
                if (!matcherdate.find()) {
                    String[] val = setUpdate[i].replace("\\", "").replace(" ", "").replace("'", "").split("=");
                    JSONFormatValue = JSONFormatValue + ",\\\"" + val[0] + "\\\":\\" + val[1].replace("'", "\\\"") + "\\\"";
                } else {
                    //System.out.println("CHECK D_UPDATE");
                    //String[] val = setUpdate[i].replace("\\", "").replace(" ","").replace("'","").split("=");
                    //JSONFormatValue = JSONFormatValue + ",\\\"" + val[0] +"\\\":\\" + "DATE_FORMAT(NOW(),'%Y%m%d%H%i%s')"+"\\\""; 
                }
                //System.out.println("JJJJJ : "+ JSONFormatValue);
            }
            //System.out.println("Full Update JSON Format : " + JSONFormatValue.replace("---,","").replace(" ", "").replace("\\\\", "\\"));
            return JSONFormatValue.replace("---,", "").replace(" ", "").replace("\\\\", "\\").replace("---", "");
        } catch (Exception e) {
            //System.err.println("Cannot Found Update!! Please Check This sql....");
            //System.err.println(sql);
            return "";
        }

        /*
        String[] updatePattern = sql.split("where");
        System.out.println("updatePattern[0] = " + updatePattern[0]);
        String[] updateValue = updatePattern[0].split("set");
        String update = updateValue[1];
        System.out.println("update = " + update);
        String[] dateFirstDiagPattern = update.split("datefirstdiag");
        String dateFirstDiag = "\"datefirstdiag\":"+"\""+dateFirstDiagPattern[1].replace("'","").replace("=","")+"\"";
        return dateFirstDiag;*/
    }

    private static String getChronicJSONUpdateValue(String sql) {
        try {
            Pattern wherePattern = Pattern.compile(".*where.*");
            Pattern setPattern = Pattern.compile(".*set.*");
            Matcher matcherWhere = wherePattern.matcher(sql);

            String[] deleteSQLWherePosition;
            if (matcherWhere.find()) {
                deleteSQLWherePosition = sql.split("where");
            } else {
                deleteSQLWherePosition = sql.split("WHERE");
            }

            Matcher matcherAnd = setPattern.matcher(deleteSQLWherePosition[0]);

            String[] attributeArray;
            if (matcherAnd.find()) {
                attributeArray = deleteSQLWherePosition[0].split("set");
            } else {
                attributeArray = deleteSQLWherePosition[0].split("SET");
            }

            //System.out.println("ATTTT : " + attributeArray[1]);
            String[] setUpdate = attributeArray[1].split(",");
            //System.out.println("Attribute String for Check Update : " + attributeArray[1]);
            //String stringForMakeJSONHashmap = deleteSQLWherePosition[1];
            //String[] checkpoint = stringForMakeJSONHashmap.split("AND");
            String JSONFormatValue = "---";
            Pattern dateupdatePattern = Pattern.compile(".*D_UPDATE.*");
            Matcher matcherdate;

            for (int i = 0; i < setUpdate.length; i++) {
                matcherdate = dateupdatePattern.matcher(setUpdate[i]);
                if (!matcherdate.find()) {
                    String[] val = setUpdate[i].replace("\\", "").replace(" ", "").replace("'", "").split("=");
                    JSONFormatValue = JSONFormatValue + ",\\\"" + val[0] + "\\\":\\\"" + val[1].replace("'", "\\\"") + "\\\"";
                } else {
                    //System.out.println("CHECK D_UPDATE");
                    //String[] val = setUpdate[i].replace("\\", "").replace(" ","").replace("'","").split("=");
                    //JSONFormatValue = JSONFormatValue + ",\\\"" + val[0] +"\\\":\\" + "DATE_FORMAT(NOW(),'%Y%m%d%H%i%s')"+"\\\""; 
                }
                //System.out.println("JJJJJ : "+ JSONFormatValue);
            }
            //System.out.println("Full Update JSON Format : " + JSONFormatValue.replace("---,","").replace(" ", "").replace("\\\\", "\\"));
            return JSONFormatValue.replace("---,", "").replace(" ", "").replace("\\\\", "\\").replace("---", "");
        } catch (Exception e) {
            //System.err.println("Cannot Found Update!! Please Check This sql....");
            //System.err.println(sql);
            return "";
        }

        /*
        String[] updatePattern = sql.split("where");
        System.out.println("updatePattern[0] = " + updatePattern[0]);
        String[] updateValue = updatePattern[0].split("set");
        String update = updateValue[1];
        System.out.println("update = " + update);
        String[] dateFirstDiagPattern = update.split("datefirstdiag");
        String dateFirstDiag = "\"datefirstdiag\":"+"\""+dateFirstDiagPattern[1].replace("'","").replace("=","")+"\"";
        return dateFirstDiag;*/
    }

    private static String getUpdateString(String keyCheck, String sql) {
        String[] temp = sql.split(" ");
        List<String> selectList = new ArrayList<String>();
        boolean select = false;

        for (int i = 0; i < temp.length; i++) {

            if (select) {
                if (temp[i].toLowerCase().equals("where")) {
                    break;
                }

                selectList.add(temp[i]);
            }

            if (temp[i].toLowerCase().equals("set")) {
                select = true;
            }

        }

        String[] selectArray = convertListToArray(selectList);
        String mergeArray = joinString(" ", selectArray);

        //System.out.println("Merge Array = " + mergeArray.toString());
        Pattern patternEquals = Pattern.compile("=");
        Pattern patternUpdate = Pattern.compile("DATE_FORMAT.*");

        Matcher matcher, dmatcher;
        matcher = patternEquals.matcher(mergeArray);

        String result = "";

        String[] compareElement;
        try {
            //System.out.println("Table on HDC for check Primary Key : " + keyCheck.toLowerCase());
            compareElement = PrimaryKeyMapping.getArrayContainerMapping(keyCheck.toLowerCase());
        } catch (Exception E) {
            System.out.println("Not Define Primary Key");
            return "";
        }

        try {
            //System.out.println("Table on HDC for check Primary Key : " + keyCheck.toLowerCase());
            compareElement = PrimaryKeyMapping.getArrayContainerMapping(keyCheck.toLowerCase());

            if (matcher.find()) {
                dmatcher = patternUpdate.matcher(mergeArray.split("=")[1].replace(" ", "").replace("'", "").replace("\\", "").replace("\"", ""));
                String key = mergeArray.split("=")[0].replace(" ", "").replace("'", "").replace("\\", "").replace("\"", "").toLowerCase();

                //System.out.println("Field for check in array is " + key);
                //System.out.println("Arrays Check : " + compareElement.toString());
                if (!Arrays.asList(compareElement).contains(key)) {
                    //System.out.println("Delete!!");
                    return "";
                }

                if (dmatcher.find()) {
                    result = "\\\"" + key.toUpperCase() + "\\\":" + mergeArray.split("=")[1].replace(" ", "").replace("'", "").replace("\\", "").replace("\"", "");
                } else {
                    result = "\\\"" + key.toUpperCase() + "\\\":\\\"" + mergeArray.split("=")[1].replace(" ", "").replace("'", "").replace("\\", "").replace("\"", "") + "\\\"";
                }
            }
        } catch (Exception E) {
            //System.out.println("Array Process Error, Cannot Process Transform this SQL");
            return "";
        }
        System.out.println("Update Data Result : " + result);
        return result;
    }

    public static String[] convertListToArray(List<String> selectList) {
        String[] selectArray = new String[selectList.size()];
        return selectList.toArray(selectArray);
    }

    private static String getJSONHashmapFormatForChronic(String value) {
        String[] deleteSQLWherePosition = value.split("WHERE");
        if (deleteSQLWherePosition.length == 1) {
            deleteSQLWherePosition = value.split("where");
        }
        String stringForMakeJSONHashmap = deleteSQLWherePosition[1];
        String[] checkpoint = stringForMakeJSONHashmap.split("AND");
        if (checkpoint.length == 1) {
            checkpoint = stringForMakeJSONHashmap.split("and");
        }
        Pattern patternEquals = Pattern.compile("=");
        Matcher matcher;
        Pattern inLower = Pattern.compile("[\\s\\t]in.*\\(");
        List<String> appendList = new ArrayList<String>();
        for (int i = 0; i < checkpoint.length; i++) {
            matcher = patternEquals.matcher(checkpoint[i]);
            if (matcher.find() || inLower.matcher(checkpoint[i]).find()) {
                if (!inLower.matcher(checkpoint[i]).find()) {
                    //System.out.println("Pattern in Found : " + checkpoint[i]);
                    appendList.add("\\\"" + checkpoint[i].split("=")[0].replace("'", "").replace(" ", "") + "\\\":\\\"" + checkpoint[i].split("=")[1].replace("\\", "").replace(" ", "").replace("'", "") + "\\\"");
                }
            }
        }
        String[] arrayJSON = convertListToArray(appendList);
        return joinString(",", arrayJSON);
    }

    private static String getJSONHashmapFormatForChronicUpdate(String value) {

        /*String[] deleteSQLWherePosition = value.split("WHERE");
        if (deleteSQLWherePosition.length == 1) {
            deleteSQLWherePosition = value.split("where");
        }
        String stringForMakeJSONHashmap = deleteSQLWherePosition[1];
        String[] checkpoint = stringForMakeJSONHashmap.split("AND");
        if (checkpoint.length == 1) {
            checkpoint = value.split("and");
        }
        Pattern patternEquals = Pattern.compile("=");
        Matcher matcher;
        Pattern inLower = Pattern.compile("[\\s\\t]in.*\\(");
        List<String> appendList = new ArrayList<String>();
        for (int i = 0; i < checkpoint.length; i++) {
            matcher = patternEquals.matcher(checkpoint[i]);
            if (matcher.find() || inLower.matcher(checkpoint[i]).find()) {
                if (!inLower.matcher(checkpoint[i]).find()) {
                    System.out.println("Pattern in Found : " + checkpoint[i]);
                    appendList.add("\\\"" + checkpoint[i].split("=")[0].replace("'", "").replace(" ", "") + "\\\":\\\"" + checkpoint[i].split("=")[1].replace("\\", "").replace(" ", "").replace("'", "") + "\\\"");
                }
            }
        }
        String[] arrayJSON = convertListToArray(appendList);
        return joinString(",", arrayJSON);*/
        Pattern wherePattern = Pattern.compile(".*where.*");
        Pattern andPattern = Pattern.compile(".*and.*");
        Matcher matcherWhere = wherePattern.matcher(value);

        String[] deleteSQLWherePosition;
        if (matcherWhere.find()) {
            deleteSQLWherePosition = value.split("where");
        } else {
            deleteSQLWherePosition = value.split("WHERE");
        }

        Matcher matcherAnd = andPattern.matcher(deleteSQLWherePosition[1]);

        String[] attributeArray;
        if (matcherAnd.find()) {
            attributeArray = deleteSQLWherePosition[1].split("and");
        } else {
            attributeArray = deleteSQLWherePosition[1].split("AND");
        }

        //String stringForMakeJSONHashmap = deleteSQLWherePosition[1];
        //String[] checkpoint = stringForMakeJSONHashmap.split("AND");
        String JSONFormatValue = "---";
        for (int i = 0; i < attributeArray.length; i++) {

            String[] val = attributeArray[i].replace("\\", "").replace(" ", "").replace("'", "").split("=");
            JSONFormatValue = JSONFormatValue + ",\\\"" + val[0] + "\\\":\\\"" + val[1].replace("'", "\\\"") + "\\\"";
            //System.out.println("JJJJJ : "+ JSONFormatValue);
        }
        //System.out.println("Full Update JSON Format : " + JSONFormatValue.replace("---,","").replace(" ", "").replace("\\\\", "\\"));
        return JSONFormatValue.replace("---,", "").replace(" ", "").replace("\\\\", "\\");
    }

    private static String getJSONHashmapFormat(String value) {
        String temp[] = value.split(" ");
        List<String> select = new ArrayList<String>();
        Boolean getSelect = false;

        for (int i = 0; i < temp.length; i++) {

            if (getSelect) {
                select.add(temp[i]);
            }

            if (temp[i].toLowerCase().equals("where")) {
                getSelect = true;
            }

        }

        String[] arraySelected = convertListToArray(select);

        String mergeArray = joinString(" ", arraySelected);

        String[] checkpoint = mergeArray.split("AND");

        Pattern patternEquals = Pattern.compile("=");
        Matcher matcher;

        Pattern inLower = Pattern.compile("[\\s\\t]in.*\\(");
        //Pattern inUpper = Pattern.compile("IN.*(");

        //Map<String, String> val = new HashMap<String, String>();
        List<String> appendList = new ArrayList<String>();

        for (int i = 0; i < checkpoint.length; i++) {
            matcher = patternEquals.matcher(checkpoint[i]);
            if (matcher.find() || inLower.matcher(checkpoint[i]).find()) {
                //val.put("\""+checkpoint[i].split("=")[0].replace("\'", "").replace(" ", "")+"\"","\""+checkpoint[i].split("=")[1].replace("\'", "").replace(" ", "")+"\"");
                //System.out.println(checkpoint[i]);
                if (inLower.matcher(checkpoint[i]).find()) {
                    //System.out.println("Pattern in Found : " + checkpoint[i]);
                    appendList.add(subArray(checkpoint[i]));
                } else {
                    appendList.add("\\\"" + checkpoint[i].split("=")[0].replace("'", "").replace(" ", "") + "\\\":\\\"" + checkpoint[i].split("=")[1].replace("\\", "").replace(" ", "").replace("'", "") + "\\\"");
                }
            }
        }

        String[] arrayJSON = convertListToArray(appendList);

        return joinString(",", arrayJSON);
    }

    public static String getSqlAnalysisType(String sql) {
        String type = getDeleteOrUpdate(sql);
        String[] table = sql.split(" ");

        if (type.toLowerCase().equals("delete")) {
            return table[2];
        } else if (type.toLowerCase().equals("update")) {
            return table[1];
        } else {
            return "";
        }
    }

    public static String getDeleteOrUpdate(String sql) {
        String[] type = sql.split(" ");
        if (type[0].toLowerCase().equals("delete")) {
            return "delete";
        } else if (type[0].toLowerCase().equals("update")) {
            return "update";
        } else {
            return "";
        }
    }

    private static String subArray(String source) {
        String key = source.split("in\\(")[0].replace(" ", "");

        if (key.equals("uCASE(diagcode)") || key.equals("Ucase(diagcode)")) {
            key = "DIAGCODE";
        }

        String value = source.split("in\\(")[1].replace(" ", "").replace(")", "").replace("'", "").replace("\\", "");
        String[] valueArray = value.split(",");

        String result = "\"\"[";

        if (valueArray.length != 0) {
            for (int i = 0; i < valueArray.length; i++) {

                result += "{\\";

                result += "\"" + key + "\\\":\\\"" + valueArray[i] + "\"";

                result += "\"}";

                if (i + 1 >= valueArray.length) {
                    break;
                } else {
                    result += ",";
                }
            }
        }
        result += "]";
        //System.out.println("Tuple Array in :" + result);
        return result;
    }

    private static String convertName(String[] elementSql) {
        for (int i = 0; i < elementSql.length; i++) {
            elementSql[i] = matchName(elementSql[i]);
        }
        return joinString(" ", elementSql);
    }

    private static String matchName(String string) {

        Pattern visitno = Pattern.compile("visitno.*");
        Pattern pcucode = Pattern.compile("pcucode.*");
        Pattern dateUpdate = Pattern.compile("dateupdate.*");

        Pattern hosservice = Pattern.compile("hosservice.*");
        Pattern nowTime = Pattern.compile("now()");

        Pattern pid = Pattern.compile("pid.*");
        Pattern personchronic = Pattern.compile("personchronic.*");

        if (personchronic.matcher(string).find() && checkPatternDup(string, "personchronic")) {
            return string.replace("personchronic", "chronic");
        }

        if (pid.matcher(string).find() && checkPatternDup(string, "pid")) {
            return string.replace("pid", "PID");
        }

        if (nowTime.matcher(string).find()) {
            return string.replace("now()", "DATE_FORMAT(NOW(),'%Y%m%d%H%i%s')");
        }

        if (hosservice.matcher(string).find() && checkPatternDup(string, "hosservice")) {
            return string.replace("hosservice", "VACCINEPLACE");
        }

        if (dateUpdate.matcher(string).find() && checkPatternDup(string, "dateupdate")) {
            return string.replace("dateupdate", "D_UPDATE");
        }

        if (visitno.matcher(string).find() && checkPatternDup(string, "visitno")) {
            return string.replace("visitno", "SEQ");
        }

        if (pcucode.matcher(string).find() && checkPatternDup(string, "pcucode")) {
            return string.replace("pcucode", "HOSPCODE");
        }

        return string;
    }

    private static String getConvertWordSQLformVisitEpi(String[] string) {
        Pattern visitepi = Pattern.compile("visitepi.*");
        Pattern vaccinecode = Pattern.compile("vaccinecode.*");
        Pattern dateepi = Pattern.compile("dateepi.*");

        for (int i = 0; i < string.length; i++) {
            if (visitepi.matcher(string[i]).find() && checkPatternDup(string[i], "visitepi")) {
                return string[i].replace("visitepi", "epi");
            }

            if (vaccinecode.matcher(string[i]).find() && checkPatternDup(string[i], "vaccinecode")) {
                return string[i].replace("vaccinecode", "VACCINETYPE");
            }

            if (dateepi.matcher(string[i]).find() && checkPatternDup(string[i], "dateepi")) {
                return string[i].replace("dateepi", "DATE_SERV");
            }
        }

        return joinString(" ", string);
    }

    private static String getConvertWordSQLformVisitDrug(String[] string) {
        Pattern visitdrug = Pattern.compile("visitdrug.*");
        Pattern unit = Pattern.compile("unit.*");
        Pattern costPrice = Pattern.compile("costprice.*");
        Pattern realPrice = Pattern.compile("realprice.*");
        Pattern drugCode = Pattern.compile("drugcode.*");

        for (int i = 0; i < string.length; i++) {

            if (drugCode.matcher(string[i]).find() && checkPatternDup(string[i], "drugcode")) {
                return string[i].replace("drugcode", "DIDSTD");
            }

            if (costPrice.matcher(string[i]).find() && checkPatternDup(string[i], "costprice")) {
                return string[i].replace("costprice", "DRUGCOST");
            }

            if (realPrice.matcher(string[i]).find() && checkPatternDup(string[i], "realprice")) {
                return string[i].replace("realprice", "DRUGPRICE");
            }

            if (unit.matcher(string[i]).find() && checkPatternDup(string[i], "unit")) {
                return string[i].replace("unit", "AMOUNT");
            }

            if (visitdrug.matcher(string[i]).find() && checkPatternDup(string[i], "visitdrug")) {
                return string[i].replace("visitdrug", "drug_opd");
            }

        }

        return joinString(" ", string);
    }

    private static String getConvertWordSQLformVisitDiag(String[] string) {
        Pattern visitdiag = Pattern.compile("visitdiag.*");
        Pattern diagCode = Pattern.compile("diagcode.*");
        Pattern ucaseCode1 = Pattern.compile("uCASE\\(diagcode\\).*");
        Pattern ucaseCode2 = Pattern.compile("Ucase\\(diagcode\\).*");
        Pattern ucaseCode3 = Pattern.compile("ucase\\(diagcode\\).*");
        Pattern ucaseCode4 = Pattern.compile("UCASE\\(diagcode\\).*");

        for (int i = 0; i < string.length; i++) {

            if (diagCode.matcher(string[i]).find() && checkPatternDup(string[i], "diagcode")) {
                return string[i].replace("diagcode", "DIAGCODE");
            }

            if (visitdiag.matcher(string[i]).find() && checkPatternDup(string[i], "visitdiag")) {
                return string[i].replace("visitdiag", "diagnosis_opd");
            }

            if (ucaseCode1.matcher(string[i]).find() || ucaseCode2.matcher(string[i]).find() || ucaseCode3.matcher(string[i]).find() || ucaseCode4.matcher(string[i]).find()) {
                //System.out.println("uCase IN!!!!");
                return string[i].replace("uCASE(diagcode)", "DIAGCODE").replace("Ucase(diagcode)", "DIAGCODE").replace("ucase(diagcode)", "DIAGCODE").replace("UCASE(diagcode)", "DIAGCODE");
            }
        }

        return joinString(" ", string);
    }

    public static String joinString(String separator, String[] stringArray) {

        if (stringArray.length >= 1) {
            for (int i = 0; i < stringArray.length; i++) {
                if (i + 1 < stringArray.length) {
                    stringArray[0] = stringArray[0] + separator + stringArray[i + 1];
                }
            }
            return stringArray[0];
        } else {
            return "";
        }
    }

    public static String delLastCharacter(String str) {
        if (str.length() > 0 && str.charAt(str.length() - 1) == 'x') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static boolean checkPatternDup(String input, String pattern) {
        Pattern perfectmatch = Pattern.compile(pattern);
        Pattern suffix = Pattern.compile("=.*");

        Matcher perfectMatcher = perfectmatch.matcher(input.toLowerCase().replace(pattern, ""));
        Matcher suffixMatcher = suffix.matcher(input.toLowerCase().replace(pattern, ""));

        /*
         System.out.println("Check Pattern string : " + input);
         System.out.println("Pattern Get : " + pattern);
         */
        if (perfectMatcher.find() || input.toLowerCase().equals(pattern)) {
            return true;
        } else {
            //System.out.println("Input delete pattern : " + input.toLowerCase().replace(pattern, ""));
            return suffixMatcher.lookingAt();
        }
    }

    private static String deleteSqlMiddlePart(String sql) {
        return "";
    }

    private static String convertDrug24(String sql) {

        boolean checkDrug24NotFound = false;

        Pattern drugcodeCheck = Pattern.compile("drugcode.*");
        Pattern drugcodePerfectMatch = Pattern.compile("drugcode");
        Pattern equalsSingle = Pattern.compile("=");
        Pattern drugcodeEqual = Pattern.compile("drugcode=");

        Matcher drugcodeMatcher = drugcodeCheck.matcher(sql);
        //System.out.println("SQL for find drugcode : " + sql);

        if (drugcodeMatcher.find()) {
            //System.out.println("drugcode MATCH!! Let's Separate!!!");
            String[] element = sql.split(" ");
            String key;
            for (int i = 0; i < element.length; i++) {

                ///////////////// NEW NEW NEW /////////////// If Error Delete It
                if (element[i].equals("(drugcode")) {
                    sql = "";
                    return sql;
                }
                //System.out.println("element check = " + element[i]);
                try {
                    if (drugcodePerfectMatch.matcher(element[i]).matches()) {
                        //System.out.println("CASE 1 drugcode perfact!!");
                        if (equalsSingle.matcher(element[i + 1]).matches()) {
                            key = element[i + 2].replace("\\", "").replace(",", "").replace("'", "");
                            if (DrugMappingCode.getDrugMappingCode().get(key) == null) {
                                checkDrug24NotFound = true;;
                            }
                            element[i + 2] = "\\'" + DrugMappingCode.getDrugMappingCode().get(key) + "\\'";
                            //System.out.println("DrugCode24 Convert Complete : " + key + " to " + element[i + 2]);
                        } else if (equalsSingle.matcher(element[i + 1]).find()) {
                            key = element[i + 1].replace("=", "").replace("\\", "").replace(",", "").replace("'", "");
                            if (DrugMappingCode.getDrugMappingCode().get(key) == null) {
                                checkDrug24NotFound = true;
                            }
                            element[i + 1] = "=\\'" + DrugMappingCode.getDrugMappingCode().get(key) + "\\'";
                            //System.out.println("DrugCode24 Convert Complete : " + key + " to " + element[i + 1]);
                        }
                    } else //System.out.println("CASE 2 drugcode unperfact!! but find");
                     if (drugcodeEqual.matcher(element[i]).matches()) {
                            //System.out.println("Element[i] + equal Match so next : " + element[i+1]);
                            key = element[i + 1].replace(",", "").replace("'", "").replace(",", "").replace("\\", "");
                            element[i + 1] = "\\'" + DrugMappingCode.getDrugMappingCode().get(key) + "\\'";
                            if (DrugMappingCode.getDrugMappingCode().get(key) == null) {
                                checkDrug24NotFound = true;
                            }
                            //System.out.println("DrugCode24 Convert Complete : " + key + " to " + element[i + 1]);
                        } else if (drugcodeEqual.matcher(element[i]).find()) {
                            //System.out.println("Drugcode + equals + drugcode " + element[i]);
                            key = element[i].replace("drugcode", "").replace("\\", "").replace(",", "").replace("'", "").replace("=", "");
                            if (DrugMappingCode.getDrugMappingCode().get(key) == null) {
                                checkDrug24NotFound = true;
                            }
                            element[i] = "drugcode=\\'" + DrugMappingCode.getDrugMappingCode().get(key) + "\\'";
                            //System.out.println("DrugCode24 Convert Complete : " + key + " to " + element[i]);
                        }

                } catch (Exception e) {
                    checkDrug24NotFound = true;
                    //System.out.println("Array Out Bound Case : drugcode Perfect Match!!");
                }

            }

            sql = joinString(" ", element);
            //System.out.println("Redrugcode to drugCode24 : " + sql);
//            if (checkDrug24NotFound) {
//                sql = "";
//               ssห //System.err.println("This drugcode can't match drugcode24 in database, Please check in cdrug table.");
//            }

        }
        return sql;
    }

    public static String checkSqlDateTime(String sql, String timecheck) {
        if (sql.equals(timecheck)) {
            return "";
        } else {
            return sql;
        }
    }

    public static String getVisitNoFromSQL(String sql) {
        Pattern seq = Pattern.compile("SEQ.*");
        String[] checkArray = sql.split(",");
//try{
        for (int i = 0; i < checkArray.length; i++) {
            if (seq.matcher(checkArray[i]).find()) {
                sql = checkArray[i].replace("SEQ", "").replace("\"", "").replace(":", "").replace("}", "").replace("{", "").replace("\\", "").replace(")", "").replace("(", "");
                //System.out.println("Can get SEQ form SQL for find date: " + sql);
            }
        }
//}catch(Exception k){
//    JOptionPane.showMessageDialog(null, "876666666666");
//    k.printStackTrace();
//}
//System.out.println("874: sql = "+sql);         
        return sql;

    }

    public static String getStringFromResultset(ResultSet rs, String column) {
//System.out.println("881 resultset::= "+rs+" ////// 881 column::= "+column);
        String str = "";
        try {
//JOptionPane.showMessageDialog(null, "884 ก่อน next ///// sql Nulllll");            
            while (rs.next()) {
                str = rs.getString(column);
//System.out.println("...XXX...Get string from formula resutset : " + str);
            }
        } catch (Exception h) {
            //JOptionPane.showMessageDialog(null, "894///// sql Nulllll");
            h.printStackTrace();
        }
        return str;
    }

    public static String getDatetimeFormSql(String sql) {
//System.out.println("903:sql = "+sql);        
        Pattern dateServ = Pattern.compile("DATE_SERV.*");
        //System.out.println("SQL Prepare for match DATE_SERV "+ sql);
        if (dateServ.matcher(sql).find()) {
            String[] arrayList = sql.split(",");
            for (int i = 0; i < arrayList.length; i++) {
                if (dateServ.matcher(arrayList[i]).find()) {
                    String value = arrayList[i].replace("\"", "").replace("{", "").replace("}", "").replace("\\", "").replace("\"", "").replace(")", "").replace("(", "");
                    value = value.split(":")[1].replace("-", "");
                    //System.out.println("Can get date form date_serv : " + value);
                    return value;
                }
            }
        }
        return "";

    }

    private static String getMultiplyDiagJSONFormat(String sql) {
        //System.out.println("Method Multiply Diag Process SQL : " + sql);

        try {
            String[] prefixSQL = sql.split("\\[");
            String[] diagcodeDialog = prefixSQL[1].split("\\]");
            //System.out.println("dialog : " + diagcodeDialog[0]);
            String[] allDiagCode = diagcodeDialog[0].split(",");
            String[] value = new String[allDiagCode.length];

            String[] sqlPatternGen = sql.split("\\\\\"DIAGCODE");
            //Pattern diagcodeMoreThan1 = Pattern.compile(".*DIAGCODE.*DIAGCODE.*");

            String prefixString = sqlPatternGen[0].replace("\"\"[{", "");
            String[] firstfixPattern = prefixString.split("\\{");
            String firstfixString = firstfixPattern[0];
            prefixString = "{" + firstfixPattern[firstfixPattern.length - 1];
            String[] suffixPattern = sqlPatternGen[sqlPatternGen.length - 1].split("}],");
            String[] postfixPattern = suffixPattern[suffixPattern.length - 1].split("}\",");
            String suffixString = "," + postfixPattern[0] + "}";
            String lastfixString = "\"," + postfixPattern[postfixPattern.length - 1];
            //System.out.println("FIRSTFIX : " + firstfixString);
            //System.out.println("PREFIX : " + prefixString);
            //System.out.println("SUFFIX : " + suffixString);
            //System.out.println("POSTFIX : " + lastfixString);

            String sqlMidGen = "";

            for (int i = 0; i < allDiagCode.length; i++) {
                String[] temp = allDiagCode[i].split(":");
                value[i] = temp[1].replace("}", "");
                //System.out.println(value[i]);           
                sqlMidGen = sqlMidGen + prefixString + "\\\"DIAGCODE\\\":" + value[i] + suffixString;
                if (i + 1 == value.length) {
                    sql = firstfixString + sqlMidGen + lastfixString;
                    break;
                }
                sqlMidGen = sqlMidGen + ",";
            }
        } catch (Exception e) {
            return sql;
        }
        return sql;
    }

}
