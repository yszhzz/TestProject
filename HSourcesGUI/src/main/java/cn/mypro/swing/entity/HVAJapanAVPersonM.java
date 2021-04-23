package cn.mypro.swing.entity;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
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

    public HVAJapanAVPersonM() {
    }
    public void setValuesAsNormal() {
        uuid = UUID.randomUUID().toString().replace("-", "");
        create_time = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
        update_time = create_time;
    }

    @Override
    public String toString() {
        return names+"-"+cname+"("+oname+")";
    }
}
