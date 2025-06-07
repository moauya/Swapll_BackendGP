package com.swapll.gradu.dto.mappers;

import com.swapll.gradu.model.Offer;
import com.swapll.gradu.model.Category;
import com.swapll.gradu.dto.OfferDTO;
import com.swapll.gradu.model.Review;

import java.util.Date;
import java.util.List;

public class OfferMapper {

    public static OfferDTO toDTO(Offer offer) {
        OfferDTO dto = new OfferDTO();
        dto.setId(offer.getId());
        dto.setTitle(offer.getTitle());
        dto.setDescription(offer.getDescription());
        dto.setPrice(offer.getPrice());
        dto.setDeliveryTime(offer.getDeliveryTime());
        dto.setPaymentMethod(offer.getPaymentMethod());
        dto.setAllowSwap(offer.isAllowSwap());
        dto.setStatus(offer.getStatus());
        dto.setType(offer.getType());
        dto.setCategoryId(offer.getCategory().getId());
        dto.setOwnerId(offer.getOwner().getId());
        dto.setCreatedAt(offer.getCreatedAt());

        if (offer.getId() != null && offer.getImage() != null) {
            dto.setImage("/api/offers/" + offer.getId() + "/image");
        }

        dto.setAverageRating(calculateAverageRating(offer.getReviews()));

        return dto;
    }

    public static Offer toEntity(OfferDTO dto, Category category) {
        Offer offer = new Offer();
        offer.setTitle(dto.getTitle());
        offer.setDescription(dto.getDescription());
        offer.setPrice(dto.getPrice() * 5);
        offer.setDeliveryTime(dto.getDeliveryTime());
        offer.setPaymentMethod(dto.getPaymentMethod());
        offer.setAllowSwap(dto.isAllowSwap());
        offer.setStatus(dto.getStatus());
        offer.setType(dto.getType());
        offer.setCategory(category);

        return offer;
    }

    private static Double calculateAverageRating(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return null;
        }

        int total = 0;
        for (Review review : reviews) {
            total += review.getRating();
        }

        return total / (double) reviews.size();
    }

}
