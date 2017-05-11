package com.opinta.dto;

import java.math.BigDecimal;

import com.opinta.constraint.EnumString;
import com.opinta.entity.DeliveryType;
import javax.validation.constraints.Size;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ShipmentDto {
    private long id;
    private long senderId;
    private long recipientId;
    @EnumString(source = DeliveryType.class)
    private DeliveryType deliveryType;
    private BigDecimal price;
    private BigDecimal postPay;
    @Size(max = 255)
    private String description;

    public ShipmentDto(long id, long senderId, long recipientId, DeliveryType deliveryType, BigDecimal price, BigDecimal postPay) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.deliveryType = deliveryType;
        this.price = price;
        this.postPay = postPay;
    }
}