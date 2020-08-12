package com.thoughtworks.rslist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "trade")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeDto {
    @Id
    private Integer rank;

    private Integer amount;

    @OneToOne
    @JoinColumn(name = "rsEventId")
    private RsEventDto rsEventDto;

}
