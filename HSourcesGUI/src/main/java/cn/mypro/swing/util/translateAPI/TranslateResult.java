package cn.mypro.swing.util.translateAPI;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class TranslateResult implements Serializable {

    @JSONField(name = "from")
    private String from;

    @JSONField(name = "to")
    private String to;

    @JSONField(name = "trans_result")
    private List<Answer> trans_result;

    public TranslateResult(String from, String to, List<Answer> trans_result) {
        this.from = from;
        this.to = to;
        this.trans_result = trans_result;
    }

    public TranslateResult() {
    }

    @Override
    public String toString() {
        return "TranslateResult{" +
                "from=" + from +
                ", to='" + to + '\'' +
                ", trans_result=" + trans_result +
                '}';
    }
}
