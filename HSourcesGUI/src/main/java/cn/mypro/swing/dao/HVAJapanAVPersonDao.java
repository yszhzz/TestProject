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

    private static String insertPersonSql = "insert into H_V_A_JAP_AV_PERSON_M(UUID,NAMES,CNAME,ONAME,GENDER,START_TIME,PHTOT_1,PHTOT_2,DETA_INFO,OTHER_INFO,SCORES,LEVELS,CREATE_TIME,UPDATE_TIME) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static String selectAllPersonSql = "select UUID,NAMES,CNAME,ONAME,GENDER,START_TIME,PHTOT_1,PHTOT_2,DETA_INFO,OTHER_INFO,SCORES,LEVELS,CREATE_TIME,UPDATE_TIME from H_V_A_JAP_AV_PERSON_M order by GENDER desc,NAMES";
    private static String selectPersonByNameSql = "select UUID,NAMES,CNAME,ONAME,GENDER,START_TIME,PHTOT_1,PHTOT_2,DETA_INFO,OTHER_INFO,SCORES,LEVELS,CREATE_TIME,UPDATE_TIME from H_V_A_JAP_AV_PERSON_M where NAMES like ? or CNAME like ? or ONAME like ? order by GENDER desc,NAMES";
    private static String selectPersonByUUIDSql = "select UUID,NAMES,CNAME,ONAME,GENDER,START_TIME,PHTOT_1,PHTOT_2,DETA_INFO,OTHER_INFO,SCORES,LEVELS,CREATE_TIME,UPDATE_TIME from H_V_A_JAP_AV_PERSON_M order by GENDER desc,NAMES";
    private static String updatePersonByUUIDSql = "update H_V_A_JAP_AV_PERSON_M set NAMES = ?,CNAME = ?,ONAME = ?,GENDER = ?,START_TIME = ?,PHTOT_1 = ? ,PHTOT_2 = ? ,DETA_INFO = ? ,OTHER_INFO = ? ,SCORES = ? ,LEVELS = ? ,UPDATE_TIME = ? where UUID = ?";
    private static String existPersonSql = "select '1' from H_V_A_JAP_AV_PERSON_M where UUID = ?";

    private static String selectPersonByMessageUUIDSql = "select a.UUID,a.NAMES,a.CNAME,a.ONAME,a.GENDER,a.START_TIME,a.PHTOT_1,a.PHTOT_2,a.DETA_INFO,a.OTHER_INFO,a.SCORES,a.LEVELS,a.CREATE_TIME,a.UPDATE_TIME from H_V_A_JAP_AV_PERSON_M a,H_V_A_JAP_AV_PERSON_C c where c.UUID_AV_PERSON_M = a.UUID and c.UUID_AV_M = ? order by a.GENDER desc,a.NAMES";

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
                personM.getUpdate_time());
    }

    public static List<HVAJapanAVPersonM> qryAll(Connection connection) throws SQLException, IOException {
        List<Map<String, Object>> allPeersons = DataBaseUtils.queryMapList(connection, selectAllPersonSql);

        List<HVAJapanAVPersonM> persons = new ArrayList<>();

        for (Map<String, Object> selectPerson : allPeersons) {

            HVAJapanAVPersonM pereson = new HVAJapanAVPersonM();
            pereson.setUuid((String) selectPerson.get("UUID"));
            pereson.setNames((String) selectPerson.get("NAMES"));
            pereson.setCname((String) selectPerson.get("CNAME"));
            pereson.setOname((String) selectPerson.get("ONAME"));
            pereson.setGender((String) selectPerson.get("GENDER"));
            pereson.setStart_time((String) selectPerson.get("START_TIME"));
            pereson.setPhtot_1(((Blob) selectPerson.get("PHTOT_1")).getBinaryStream().readAllBytes());
            pereson.setPhtot_2(((Blob) selectPerson.get("PHTOT_2")).getBinaryStream().readAllBytes());
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
            pereson.setStart_time((String) selectPerson.get("START_TIME"));
            pereson.setPhtot_1(((Blob) selectPerson.get("PHTOT_1")).getBinaryStream().readAllBytes());
            pereson.setPhtot_2(((Blob) selectPerson.get("PHTOT_2")).getBinaryStream().readAllBytes());
            pereson.setDeta_info((String) selectPerson.get("DETA_INFO"));
            pereson.setOther_info(((String) selectPerson.get("OTHER_INFO")));
            pereson.setScores(((BigDecimal) selectPerson.get("SCORES")).longValue());
            pereson.setLevels((String) selectPerson.get("LEVELS"));
            pereson.setCreate_time((String) selectPerson.get("CREATE_TIME"));
            pereson.setUpdate_time((String) selectPerson.get("UPDATE_TIME"));
            pereson.setHave(true);
            persons.add(pereson);
        }
        return persons;
    }

    public static HVAJapanAVPersonM qryOnePersonByUUID(Connection connection, String uuid) throws SQLException, IOException {
        Map<String, Object> selectPerson = DataBaseUtils.queryMap(connection, selectPersonByUUIDSql, 1, uuid);

        HVAJapanAVPersonM pereson = new HVAJapanAVPersonM();
        pereson.setUuid((String) selectPerson.get("UUID"));
        pereson.setNames((String) selectPerson.get("NAMES"));
        pereson.setCname((String) selectPerson.get("CNAME"));
        pereson.setOname((String) selectPerson.get("ONAME"));
        pereson.setGender((String) selectPerson.get("GENDER"));
        pereson.setStart_time((String) selectPerson.get("START_TIME"));
        pereson.setPhtot_1(((Blob) selectPerson.get("PHTOT_1")).getBinaryStream().readAllBytes());
        pereson.setPhtot_2(((Blob) selectPerson.get("PHTOT_2")).getBinaryStream().readAllBytes());
        pereson.setDeta_info((String) selectPerson.get("DETA_INFO"));
        pereson.setOther_info(((String) selectPerson.get("OTHER_INFO")));
        pereson.setScores(((BigDecimal) selectPerson.get("SCORES")).longValue());
        pereson.setLevels((String) selectPerson.get("LEVELS"));
        pereson.setCreate_time((String) selectPerson.get("CREATE_TIME"));
        pereson.setUpdate_time((String) selectPerson.get("UPDATE_TIME"));
        pereson.setHave(true);

        return pereson;
    }

    public static void updatePerson(Connection connection, HVAJapanAVPersonM personM) throws SQLException {
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
                personM.getUuid());
    }

    public static List<HVAJapanAVPersonM> qryPersonByMessageUUID(Connection connection, String messageUUID) throws SQLException, IOException {
        List<Map<String, Object>> messagePersons = DataBaseUtils.queryMapList(connection, selectPersonByMessageUUIDSql,messageUUID);

        List<HVAJapanAVPersonM> persons = new ArrayList<>();

        for (Map<String, Object> selectPerson : messagePersons) {

            HVAJapanAVPersonM pereson = new HVAJapanAVPersonM();
            pereson.setUuid((String) selectPerson.get("UUID"));
            pereson.setNames((String) selectPerson.get("NAMES"));
            pereson.setCname((String) selectPerson.get("CNAME"));
            pereson.setOname((String) selectPerson.get("ONAME"));
            pereson.setGender((String) selectPerson.get("GENDER"));
            pereson.setStart_time((String) selectPerson.get("START_TIME"));
            pereson.setPhtot_1(((Blob) selectPerson.get("PHTOT_1")).getBinaryStream().readAllBytes());
            pereson.setPhtot_2(((Blob) selectPerson.get("PHTOT_2")).getBinaryStream().readAllBytes());
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

    public static void mergePersonMessageC(Connection connection, HVAJapanAVM avm) throws SQLException {
        for (HVAJapanAVPersonM person : avm.getPersons()) {
            DataBaseUtils.executeUpdate(connection,mergePersonMessageCSql,avm.getUuid(),person.getUuid(),
                    avm.getUuid(),
                    person.getUuid(),
                    new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()));

        }
    }
}
