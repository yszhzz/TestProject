package cn.mypro.swing.entity;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.UUID;

@Getter
@Setter
public class HVAJapanAVPersonM {

    private String uuid;
    private String names;
    private String cname;
    private String oname;
    private String gender;
    private String start_time;
    private byte[] phtot_1;
    private byte[] phtot_2;
    private String deta_info;
    private String other_info;
    private long scores;
    private String levels;
    private String create_time;
    private String update_time;
    private boolean have = false;
    private String robot = "0";

    public HVAJapanAVPersonM() {
    }
    public void setValuesAsNormal() {
        uuid = UUID.randomUUID().toString().replace("-", "");
        create_time = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
        update_time = create_time;
    }

    @Override
    public String toString() {
        return ("1".equals(gender)?"W":"M") + " - " + names+"-"+cname+"("+oname+")";
    }

    public String getAllMessageString() {
        return "HVAJapanAVPersonM{" +
                "uuid='" + uuid + '\'' +
                ", names='" + names + '\'' +
                ", cname='" + cname + '\'' +
                ", oname='" + oname + '\'' +
                ", gender='" + gender + '\'' +
                ", start_time='" + start_time + '\'' +
                ", phtot_1=" + Arrays.toString(phtot_1) +
                ", phtot_2=" + Arrays.toString(phtot_2) +
                ", deta_info='" + deta_info + '\'' +
                ", other_info='" + other_info + '\'' +
                ", scores=" + scores +
                ", levels='" + levels + '\'' +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
                ", robot='" + robot + '\'' +
                ", have=" + have +
                '}';
    }
}
