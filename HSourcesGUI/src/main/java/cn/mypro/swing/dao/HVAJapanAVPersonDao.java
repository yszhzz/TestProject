package cn.mypro.swing.dao;

import cn.mypro.swing.entity.HVAJapanAVLabelM;
import cn.mypro.swing.entity.HVAJapanAVM;
import cn.mypro.swing.entity.HVAJapanAVPersonM;
import cn.mypro.utils.DataBaseUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HVAJapanAVPersonDao {

    private static String insertPersonSql = "insert into H_V_A_JAP_AV_PERSON_M(UUID,NAMES,CNAME,ONAME,GENDER,START_TIME,PHTOT_1,PHTOT_2,DETA_INFO,OTHER_INFO,SCORES,LEVELS,CREATE_TIME,UPDATE_TIME,ROBOT) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static String selectAllPersonSql = "select UUID,NAMES,CNAME,ONAME,GENDER,START_TIME,PHTOT_1,PHTOT_2,DETA_INFO,OTHER_INFO,SCORES,LEVELS,CREATE_TIME,UPDATE_TIME,ROBOT from H_V_A_JAP_AV_PERSON_M order by GENDER,NAMES";
    private static String selectAllPersonPartSql = "select UUID,NAMES,CNAME,ONAME,GENDER,SCORES,LEVELS,ROBOT from H_V_A_JAP_AV_PERSON_M order by GENDER,NAMES";

    private static String selectPersonByNameSql = "select UUID,NAMES,CNAME,ONAME,GENDER,START_TIME,PHTOT_1,PHTOT_2,DETA_INFO,OTHER_INFO,SCORES,LEVELS,CREATE_TIME,UPDATE_TIME,ROBOT from H_V_A_JAP_AV_PERSON_M where NAMES like ? or CNAME like ? or ONAME like ? order by GENDER desc,NAMES";
    private static String selectPersonByNamesSql = "select UUID,NAMES,CNAME,ONAME,GENDER,START_TIME,PHTOT_1,PHTOT_2,DETA_INFO,OTHER_INFO,SCORES,LEVELS,CREATE_TIME,UPDATE_TIME,ROBOT from H_V_A_JAP_AV_PERSON_M where NAMES like ? order by GENDER desc,NAMES";

    private static String selectPersonByUUIDSql = "select UUID,NAMES,CNAME,ONAME,GENDER,START_TIME,PHTOT_1,PHTOT_2,DETA_INFO,OTHER_INFO,SCORES,LEVELS,CREATE_TIME,UPDATE_TIME,ROBOT from H_V_A_JAP_AV_PERSON_M where UUID = ?";
    private static String updatePersonByUUIDSql = "update H_V_A_JAP_AV_PERSON_M set NAMES = ?,CNAME = ?,ONAME = ?,GENDER = ?,START_TIME = ?,PHTOT_1 = ? ,PHTOT_2 = ? ,DETA_INFO = ? ,OTHER_INFO = ? ,SCORES = ? ,LEVELS = ? ,UPDATE_TIME = ?, ROBOT = ? where UUID = ?";
    private static String existPersonSql = "select '1' from H_V_A_JAP_AV_PERSON_M where UUID = ?";
    private static String deleteAllByMUUID = "delete from H_V_A_JAP_AV_PERSON_C where UUID_AV_M = ?";

    private static String selectPersonByMessageUUIDSql = "select a.UUID,a.NAMES,a.CNAME,a.ONAME,a.GENDER,a.START_TIME,a.PHTOT_1,a.PHTOT_2,a.DETA_INFO,a.OTHER_INFO,a.SCORES,a.LEVELS,a.CREATE_TIME,a.UPDATE_TIME,a.ROBOT from H_V_A_JAP_AV_PERSON_M a,H_V_A_JAP_AV_PERSON_C c where c.UUID_AV_PERSON_M = a.UUID and c.UUID_AV_M = ? order by a.GENDER desc,a.NAMES";

    private static String mergePersonMessageCSql = "merge into H_V_A_JAP_AV_PERSON_C using dual on (UUID_AV_M = ? and UUID_AV_PERSON_M = ?) WHEN NOT MATCHED THEN insert values(?,?,?)";

    public static void insertPerson(Connection connection, HVAJapanAVPersonM personM) throws SQLException {
        DataBaseUtils.executeUpdate(connection, insertPersonSql,
                personM.getUuid(),
                personM.getNames(),
                personM.getCname(),
                personM.getOname(),
                personM.getGender(),
                personM.getStart_time(),
                personM.getPhtot_1(),
                personM.getPhtot_2(),
                personM.getDeta_info(),
                personM.getOther_info(),
                personM.getScores(),
                personM.getLevels(),
                personM.getCreate_time(),
                personM.getUpdate_time(),
                personM.getRobot());
    }
    public static void updatePerson(Connection connection, HVAJapanAVPersonM personM) throws SQLException {
        System.out.println(personM.getPhtot_2());
        DataBaseUtils.executeUpdate(connection, updatePersonByUUIDSql,
                personM.getNames(),
                personM.getCname(),
                personM.getOname(),
                personM.getGender(),
                personM.getStart_time(),
                personM.getPhtot_1(),
                personM.getPhtot_2(),
                personM.getDeta_info(),
                personM.getOther_info(),
                personM.getScores(),
                personM.getLevels(),
                personM.getUpdate_time(),
                personM.getRobot(),
                personM.getUuid());
    }

    public static List<HVAJapanAVPersonM> qryAllPersonMessage(Connection connection) throws SQLException, IOException {
        List<Map<String, Object>> allPeersons = DataBaseUtils.queryMapList(connection, selectAllPersonSql);

        if (allPeersons == null || allPeersons.size() == 0) return null;

        List<HVAJapanAVPersonM> persons = new ArrayList<>();
        for (Map<String, Object> selectPerson : allPeersons) {

            HVAJapanAVPersonM pereson = new HVAJapanAVPersonM();

            pereson.setUuid((String) selectPerson.get("UUID"));
            pereson.setNames((String) selectPerson.get("NAMES"));
            pereson.setCname((String) selectPerson.get("CNAME"));
            pereson.setOname((String) selectPerson.get("ONAME"));
            pereson.setGender((String) selectPerson.get("GENDER"));
            pereson.setRobot((String) selectPerson.get("ROBOT"));
            //优化效率
            pereson.setStart_time((String) selectPerson.get("START_TIME"));
            Blob phtot_1 = (Blob) selectPerson.get("PHTOT_1");
            if (phtot_1 != null) pereson.setPhtot_1(phtot_1.getBinaryStream().readAllBytes());
            Blob phtot_2 = (Blob) selectPerson.get("PHTOT_2");
            if (phtot_2 != null) pereson.setPhtot_2(phtot_2.getBinaryStream().readAllBytes());
            pereson.setDeta_info((String) selectPerson.get("DETA_INFO"));
            pereson.setOther_info(((String) selectPerson.get("OTHER_INFO")));
            pereson.setScores(((BigDecimal) selectPerson.get("SCORES")).longValue());
            pereson.setLevels((String) selectPerson.get("LEVELS"));
            pereson.setCreate_time((String) selectPerson.get("CREATE_TIME"));
            pereson.setUpdate_time((String) selectPerson.get("UPDATE_TIME"));
            persons.add(pereson);
        }
        return persons;
    }

    public static List<HVAJapanAVPersonM> qryAllSimplePersonMessage(Connection connection) throws SQLException, IOException {
        List<Map<String, Object>> allPeersons = DataBaseUtils.queryMapList(connection, selectAllPersonPartSql);

        if (allPeersons == null || allPeersons.size() == 0) return null;

        List<HVAJapanAVPersonM> persons = new ArrayList<>();
        for (Map<String, Object> selectPerson : allPeersons) {

            HVAJapanAVPersonM pereson = new HVAJapanAVPersonM();

            pereson.setUuid((String) selectPerson.get("UUID"));
            pereson.setNames((String) selectPerson.get("NAMES"));
            pereson.setCname((String) selectPerson.get("CNAME"));
            pereson.setOname((String) selectPerson.get("ONAME"));
            pereson.setGender((String) selectPerson.get("GENDER"));
            pereson.setRobot((String) selectPerson.get("ROBOT"));
            //优化效率
            pereson.setScores(((BigDecimal) selectPerson.get("SCORES")).longValue());
            pereson.setLevels((String) selectPerson.get("LEVELS"));
            persons.add(pereson);
        }
        return persons;
    }

    public static List<HVAJapanAVPersonM> qryPersonByName(Connection connection, String partOfName) throws SQLException, IOException {
        List<Map<String, Object>> allPeersons = DataBaseUtils.queryMapList(connection, selectPersonByNameSql, "%" + partOfName + "%", "%" + partOfName + "%", "%" + partOfName + "%");

        List<HVAJapanAVPersonM> persons = new ArrayList<>();

        for (Map<String, Object> selectPerson : allPeersons) {

            HVAJapanAVPersonM pereson = new HVAJapanAVPersonM();
            pereson.setUuid((String) selectPerson.get("UUID"));
            pereson.setNames((String) selectPerson.get("NAMES"));
            pereson.setCname((String) selectPerson.get("CNAME"));
            pereson.setOname((String) selectPerson.get("ONAME"));
            pereson.setGender((String) selectPerson.get("GENDER"));
/*            pereson.setStart_time((String) selectPerson.get("START_TIME"));

            Blob phtot_1 = (Blob) selectPerson.get("PHTOT_1");
            if (phtot_1 != null) pereson.setPhtot_1(phtot_1.getBinaryStream().readAllBytes());
            Blob phtot_2 = (Blob) selectPerson.get("PHTOT_2");
            if (phtot_2 != null) pereson.setPhtot_2(phtot_2.getBinaryStream().readAllBytes());
            pereson.setDeta_info((String) selectPerson.get("DETA_INFO"));
            pereson.setOther_info(((String) selectPerson.get("OTHER_INFO")));*/
            pereson.setScores(((BigDecimal) selectPerson.get("SCORES")).longValue());
            pereson.setLevels((String) selectPerson.get("LEVELS"));
            pereson.setCreate_time((String) selectPerson.get("CREATE_TIME"));
            pereson.setUpdate_time((String) selectPerson.get("UPDATE_TIME"));
            pereson.setRobot((String) selectPerson.get("ROBOT"));
            pereson.setHave(true);
            persons.add(pereson);
        }
        return persons;
    }

    public static HVAJapanAVPersonM qryPersonByNames(Connection connection, String partOfName) throws SQLException {

        Map<String, Object> selectPerson = DataBaseUtils.queryMap(connection, selectPersonByNamesSql, "%" + partOfName + "%");
        HVAJapanAVPersonM pereson = null;
        if (selectPerson != null) {
            pereson = new HVAJapanAVPersonM();
            pereson.setUuid((String) selectPerson.get("UUID"));
            pereson.setNames((String) selectPerson.get("NAMES"));
            pereson.setCname((String) selectPerson.get("CNAME"));
            pereson.setOname((String) selectPerson.get("ONAME"));
            pereson.setGender((String) selectPerson.get("GENDER"));

            pereson.setScores(((BigDecimal) selectPerson.get("SCORES")).longValue());
            pereson.setLevels((String) selectPerson.get("LEVELS"));
            pereson.setCreate_time((String) selectPerson.get("CREATE_TIME"));
            pereson.setUpdate_time((String) selectPerson.get("UPDATE_TIME"));
            pereson.setRobot((String) selectPerson.get("ROBOT"));
            pereson.setHave(true);
        }
        return pereson;
    }


    public static HVAJapanAVPersonM qryOnePersonByUUID(Connection connection, String uuid) throws SQLException, IOException {
        Map<String, Object> selectPerson = DataBaseUtils.queryMap(connection, selectPersonByUUIDSql, uuid);

        HVAJapanAVPersonM pereson = null;

        if (selectPerson != null) {
            pereson = new HVAJapanAVPersonM();
            pereson.setUuid((String) selectPerson.get("UUID"));
            pereson.setNames((String) selectPerson.get("NAMES"));
            pereson.setCname((String) selectPerson.get("CNAME"));
            pereson.setOname((String) selectPerson.get("ONAME"));
            pereson.setGender((String) selectPerson.get("GENDER"));
            pereson.setStart_time((String) selectPerson.get("START_TIME"));
            Blob phtot_1 = (Blob) selectPerson.get("PHTOT_1");
            if (phtot_1 != null) pereson.setPhtot_1(phtot_1.getBinaryStream().readAllBytes());
            Blob phtot_2 = (Blob) selectPerson.get("PHTOT_2");
            if (phtot_2 != null) pereson.setPhtot_2(phtot_2.getBinaryStream().readAllBytes());
            pereson.setDeta_info((String) selectPerson.get("DETA_INFO"));
            pereson.setOther_info(((String) selectPerson.get("OTHER_INFO")));
            pereson.setScores(((BigDecimal) selectPerson.get("SCORES")).longValue());
            pereson.setLevels((String) selectPerson.get("LEVELS"));
            pereson.setCreate_time((String) selectPerson.get("CREATE_TIME"));
            pereson.setUpdate_time((String) selectPerson.get("UPDATE_TIME"));
            pereson.setRobot((String) selectPerson.get("ROBOT"));
            pereson.setHave(true);
        }

        return pereson;
    }



    public static List<HVAJapanAVPersonM> qryPersonByMessageUUID(Connection connection, String messageUUID) throws SQLException, IOException {
        List<Map<String, Object>> messagePersons = DataBaseUtils.queryMapList(connection, selectPersonByMessageUUIDSql, messageUUID);

        List<HVAJapanAVPersonM> persons = new ArrayList<>();

        for (Map<String, Object> selectPerson : messagePersons) {

            HVAJapanAVPersonM pereson = new HVAJapanAVPersonM();
            pereson.setUuid((String) selectPerson.get("UUID"));
            pereson.setNames((String) selectPerson.get("NAMES"));
            pereson.setCname((String) selectPerson.get("CNAME"));
            pereson.setOname((String) selectPerson.get("ONAME"));
            pereson.setGender((String) selectPerson.get("GENDER"));
/*            pereson.setStart_time((String) selectPerson.get("START_TIME"));
            Blob phtot_1 = (Blob) selectPerson.get("PHTOT_1");
            if (phtot_1 != null) pereson.setPhtot_1(phtot_1.getBinaryStream().readAllBytes());
            Blob phtot_2 = (Blob) selectPerson.get("PHTOT_2");
            if (phtot_2 != null) pereson.setPhtot_2(phtot_2.getBinaryStream().readAllBytes());
            pereson.setDeta_info((String) selectPerson.get("DETA_INFO"));
            pereson.setOther_info(((String) selectPerson.get("OTHER_INFO")));*/
            pereson.setScores(((BigDecimal) selectPerson.get("SCORES")).longValue());
            pereson.setLevels((String) selectPerson.get("LEVELS"));
            pereson.setCreate_time((String) selectPerson.get("CREATE_TIME"));
            pereson.setUpdate_time((String) selectPerson.get("UPDATE_TIME"));
            pereson.setRobot((String) selectPerson.get("ROBOT"));
            persons.add(pereson);
        }
        return persons;
    }

    public static void mergePersonMessageC(Connection connection, HVAJapanAVM avm) throws SQLException {

        DataBaseUtils.executeUpdate(connection, deleteAllByMUUID, avm.getUuid());

        if (avm.getPersons() == null) return;
        for (HVAJapanAVPersonM person : avm.getPersons()) {
            DataBaseUtils.executeUpdate(connection, mergePersonMessageCSql, avm.getUuid(), person.getUuid(),
                    avm.getUuid(),
                    person.getUuid(),
                    new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()));

        }
    }
}
