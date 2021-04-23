package cn.mypro.swing.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;
import java.util.UUID;

@Getter
@Setter
public class HVAJapanAVLabelM {

    private String uuid;
    private String type_1;
    private String type_2;
    private String type_3;
    private String type_4;
    private String type_5;
    private String label_code;
    private String label_show;
    private String label_comment;
    private long label_count;
    private String label_level;
    private String create_time;
    private String update_time;



    public HVAJapanAVLabelM() {
    }
    public HVAJapanAVLabelM(String uuid, String create_time, String update_time) {
        this.uuid = uuid;
        this.create_time = create_time;
        this.update_time = update_time;
    }
    public void setValuesAsNormal() {
        uuid = UUID.randomUUID().toString().replace("-", "");
        label_count = 0;
        label_level = "H";
        create_time = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
        update_time = create_time;
    }
    public void setCommentAsNormal() {
        label_comment = "As Show:"+label_show;
    }

    @Override
    public String toString() {
        return label_code + ":" + label_show;
    }



}
