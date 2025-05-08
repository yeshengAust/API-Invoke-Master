package com.yes.log.domain.vo;

import com.yes.common.domain.entity.Log;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayDataVo {
    private List<String> days;
    private List<Long> data;
}
