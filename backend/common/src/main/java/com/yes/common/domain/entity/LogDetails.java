package com.yes.common.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogDetails  implements Serializable {
    private  Long  logId;
    private  String requestParams ;
    private  String response ;
}
