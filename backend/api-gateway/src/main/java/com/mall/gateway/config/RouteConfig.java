package com.mall.gateway.config;
        
import com.mall.gateway.filter.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

    private final AuthFilter authFilter;

    @Value("${services.user-url}") private String userServiceUrl;
    @Value("${services.product-url}") private String productServiceUrl;
    @Value("${services.order-url}") private String orderServiceUrl;
    @Value("${services.payment-url}") private String paymentServiceUrl;
    @Value("${services.vendor-url}") private String vendorServiceUrl;
    @Value("${services.inventory-url}") private String inventoryServiceUrl;
    @Value("${services.notification-url}") private String notificationServiceUrl;
    @Value("${services.shipping-url}") private String shippingServiceUrl;
    @Value("${services.review-url}") private String reviewServiceUrl;
    @Value("${services.discount-url}") private String discountServiceUrl;
    @Value("${services.wishlist-url}") private String wishlistServiceUrl;
    @Value("${services.admin-url}") private String adminServiceUrl;
    @Value("${services.search-url}") private String searchServiceUrl;
    @Value("${services.media-url}") private String mediaServiceUrl;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth routes — public, no auth filter
                .route("user-auth", r -> r.path("/api/v1/auth/**")
                        .uri(userServiceUrl))

                // OAuth2 routes — public
                .route("oauth2", r -> r.path("/login/oauth2/**", "/oauth2/**")
                        .uri(userServiceUrl))

                // User service — protected
                .route("user-service", r -> r.path("/api/v1/users/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                        .uri(userServiceUrl))

                // Product service
                .route("product-service", r -> r.path("/api/v1/products/**", "/api/v1/categories/**")
                        .uri(productServiceUrl))

                // Order service — protected
                .route("order-service", r -> r.path("/api/v1/orders/**", "/api/v1/cart/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                        .uri(orderServiceUrl))

                // Payment service — protected
                .route("payment-service", r -> r.path("/api/v1/payments/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                        .uri(paymentServiceUrl))

                // Stripe webhooks — no auth
                .route("stripe-webhook", r -> r.path("/api/v1/webhooks/stripe")
                        .uri(paymentServiceUrl))

                // Vendor service
                .route("vendor-service", r -> r.path("/api/v1/vendors/**", "/api/v1/shops/**")
                        .uri(vendorServiceUrl))

                // Inventory service
                .route("inventory-service", r -> r.path("/api/v1/inventory/**")
                        .uri(inventoryServiceUrl))

                // Notification service
                .route("notification-service", r -> r.path("/api/v1/notifications/**")
                        .uri(notificationServiceUrl))

                // Shipping service
                .route("shipping-service", r -> r.path("/api/v1/shipments/**")
                        .uri(shippingServiceUrl))

                // Review service
                .route("review-service", r -> r.path("/api/v1/reviews/**", "/api/v1/questions/**")
                        .uri(reviewServiceUrl))

                // Discount service
                .route("discount-service", r -> r.path("/api/v1/coupons/**", "/api/v1/flash-sales/**")
                        .uri(discountServiceUrl))

                // Wishlist service
                .route("wishlist-service", r -> r.path("/api/v1/wishlist/**")
                        .uri(wishlistServiceUrl))

                // Admin service — ROLE_ADMIN only
                .route("admin-service", r -> r.path("/api/v1/admin/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                        .uri(adminServiceUrl))

                // Search service — public
                .route("search-service", r -> r.path("/api/v1/search/**")
                        .uri(searchServiceUrl))

                // Media service
                .route("media-service", r -> r.path("/api/v1/media/**")
                        .uri(mediaServiceUrl))

                .build();
    }
}
