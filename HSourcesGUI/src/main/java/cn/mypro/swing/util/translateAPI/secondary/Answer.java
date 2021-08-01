package cn.mypro.swing.util.translateAPI.secondary;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
public class Answer implements Serializable {
    @JSONField(name = "src")
    private String src;

    @JSONField(name = "dst")
    private String dst;

    public Answer(String src, String dst) {
        this.src = src;
        this.dst = dst;
    }

    public Answer() {
    }

    @Override
    public String toString() {
        return "Answer{" +
                "src=" + src +
                ", dst='" + dst + '\'' +
                '}';
    }
}
