package com.eagledev.bookreaders.mappers;

import com.eagledev.bookreaders.dtos.commerce.CartItemResponse;
import com.eagledev.bookreaders.dtos.commerce.CartResponse;
import com.eagledev.bookreaders.entities.Cart;
import com.eagledev.bookreaders.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {

    @Mapping(target = "userUuid", source = "user.uuid")
    CartResponse cartToCartResponse(Cart cart);

    @Mapping(target = "bookUuid", source = "book.uuid")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "price", source = "book.price")
    CartItemResponse cartItemToCartItemResponse(CartItem cartItem);
}

