package com.eagledev.bookreaders.mappers;

import com.eagledev.bookreaders.dtos.commerce.OrderItemResponse;
import com.eagledev.bookreaders.dtos.commerce.OrderResponse;
import com.eagledev.bookreaders.entities.Order;
import com.eagledev.bookreaders.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(target = "userUuid", source = "user.uuid")
    OrderResponse orderToOrderResponse(Order order);

    @Mapping(target = "bookUuid", source = "book.uuid")
    @Mapping(target = "bookTitle", source = "book.title")
    OrderItemResponse orderItemToOrderItemResponse(OrderItem orderItem);
}

