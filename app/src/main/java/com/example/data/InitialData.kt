package com.example.data

import com.example.model.Brand
import com.example.model.Category
import com.example.model.Coupon
import com.example.model.FlashDeal
import com.example.model.Product
import com.example.model.Review

object InitialData {

    val categories = listOf(
        Category(
            id = "cat_tech",
            name = "Electronics & Tech",
            slug = "electronics-tech",
            description = "Cutting-edge mobile devices, laptops, and smart gear",
            iconName = "Devices",
            itemCount = 18
        ),
        Category(
            id = "cat_audio",
            name = "Audio & Sound",
            slug = "audio-sound",
            description = "Studio monitor headphones, soundbars, and wireless earbuds",
            iconName = "Headphones",
            itemCount = 12
        ),
        Category(
            id = "cat_fashion",
            name = "Fashion & Apparel",
            slug = "fashion-apparel",
            description = "Minimalist urban outerwear, knitwear, and everyday essentials",
            iconName = "Checkroom",
            itemCount = 24
        ),
        Category(
            id = "cat_wearables",
            name = "Smart Wearables",
            slug = "smart-wearables",
            description = "Health trackers, biometric smartwatches, and smart rings",
            iconName = "Watch",
            itemCount = 9
        ),
        Category(
            id = "cat_accessories",
            name = "Lifestyle & Gear",
            slug = "lifestyle-gear",
            description = "Premium commuter bags, EDC knives, and titanium accessories",
            iconName = "WorkOutline",
            itemCount = 15
        )
    )

    val brands = listOf(
        Brand("b_at_tech", "AT Core Tech", "at-core-tech", "", "Precision engineered electronics"),
        Brand("b_at_sound", "AT Acoustic Labs", "at-acoustic-labs", "", "Audiophile wireless acoustics"),
        Brand("b_at_luxe", "AT Luxe Studio", "at-luxe-studio", "", "Contemporary minimalist fashion"),
        Brand("b_at_orbit", "Orbit Dynamics", "orbit-dynamics", "", "Next-generation smart wearables"),
        Brand("b_at_urban", "Urban Nomad Co", "urban-nomad-co", "", "Durable urban commuter accessories")
    )

    val initialProducts = listOf(
        Product(
            id = "prod_1",
            name = "AT Studio Pro Wireless Noise-Cancelling Headphones",
            slug = "at-studio-pro-wireless-headphones",
            brand = "AT Acoustic Labs",
            category = "Audio & Sound",
            categorySlug = "audio-sound",
            sku = "AT-AUD-901",
            price = 279.00,
            originalPrice = 349.00,
            discountPercent = 20,
            stock = 45,
            rating = 4.9,
            reviewCount = 142,
            images = listOf(
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
                "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=800&q=80",
                "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=800&q=80"
            ),
            thumbnailUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
            description = "Engineered with 40mm titanium drivers and hybrid active noise cancellation up to 42dB. Experience crystal-clear spatial audio, 60 hours of continuous battery life, and ultra-plush memory foam earcups.",
            specifications = mapOf(
                "Driver Size" to "40mm Custom Titanium",
                "ANC Performance" to "-42dB Active Cancellation",
                "Battery Life" to "60 Hours (ANC Off) / 45 Hours (ANC On)",
                "Bluetooth" to "Version 5.4 with LDAC & aptX HD",
                "Weight" to "245 grams",
                "Warranty" to "2 Years Comprehensive Replacement"
            ),
            features = listOf(
                "Adaptive Hybrid Noise Cancellation",
                "Multipoint Bluetooth Connection (Pair 2 devices)",
                "Lossless USB-C Audio Playback Mode",
                "Fast Charge: 10 mins gives 5 hours playtime"
            ),
            colors = listOf("Midnight Black", "Platinum Silver", "Navy Indigo"),
            sizes = listOf("Standard Fit"),
            tags = listOf("audio", "wireless", "anc", "flagship"),
            badge = "BESTSELLER",
            isFeatured = true,
            isBestSeller = true,
            isFlashDeal = true
        ),
        Product(
            id = "prod_2",
            name = "AT Horizon Ultra Titanium Smartwatch",
            slug = "at-horizon-ultra-titanium-smartwatch",
            brand = "Orbit Dynamics",
            category = "Smart Wearables",
            categorySlug = "smart-wearables",
            sku = "AT-WR-502",
            price = 329.00,
            originalPrice = 399.00,
            discountPercent = 17,
            stock = 28,
            rating = 4.8,
            reviewCount = 98,
            images = listOf(
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&q=80",
                "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800&q=80",
                "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=800&q=80"
            ),
            thumbnailUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&q=80",
            description = "Grade-5 aerospace titanium casing with a sapphire crystal touch display. Features dual-frequency GPS, medical-grade SpO2 and ECG tracking, and 100m water resistance.",
            specifications = mapOf(
                "Case Material" to "Grade 5 Aerospace Titanium",
                "Display" to "1.43\" AMOLED 3000-nit Peak Brightness",
                "Battery" to "Up to 14 Days Typical Use",
                "Water Resistance" to "10 ATM (100 meters)",
                "Sensors" to "ECG, Optical Heart Rate, SpO2, Skin Temp, Barometer"
            ),
            features = listOf(
                "Always-On Ultra-Bright Retina AMOLED Display",
                "Dual-Frequency L1/L5 Precision GPS",
                "Over 120 Sports & Endurance Modes",
                "Offline Topographic Mapping & Compass"
            ),
            colors = listOf("Raw Titanium", "Space Gray", "Starlight Gold"),
            sizes = listOf("44mm", "47mm"),
            tags = listOf("smartwatch", "wearable", "fitness", "gps"),
            badge = "HOT",
            isFeatured = true,
            isNewArrival = true
        ),
        Product(
            id = "prod_3",
            name = "AT Pulse Nova 5G Flagship Smartphone",
            slug = "at-pulse-nova-5g-smartphone",
            brand = "AT Core Tech",
            category = "Electronics & Tech",
            categorySlug = "electronics-tech",
            sku = "AT-PHN-101",
            price = 899.00,
            originalPrice = 999.00,
            discountPercent = 10,
            stock = 15,
            rating = 4.9,
            reviewCount = 210,
            images = listOf(
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&q=80",
                "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=800&q=80"
            ),
            thumbnailUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&q=80",
            description = "Uncompromising performance featuring an octa-core 3nm chipset, 200MP periscope triple-camera system, and a 6.8\" QHD+ 144Hz LTPO fluid screen with 5500mAh silicon-carbon battery.",
            specifications = mapOf(
                "Chipset" to "Next-Gen 3nm Octa-Core Processor",
                "RAM / Storage" to "12GB / 256GB UFS 4.0",
                "Display" to "6.8-inch QHD+ 144Hz Dynamic AMOLED",
                "Camera" to "200MP Main + 50MP Periscope 5x + 50MP Ultrawide",
                "Battery & Charging" to "5500mAh with 100W HyperCharge"
            ),
            features = listOf(
                "200MP Pro-Grade Triple Camera with Optical Stabilization",
                "All-Day 5500mAh Silicon-Carbon Battery",
                "IP68 Submersion Water and Dust Resistance",
                "Satellite SOS Emergency Messaging"
            ),
            colors = listOf("Obsidian Black", "Cobalt Sky", "Emerald Green"),
            sizes = listOf("256GB", "512GB"),
            tags = listOf("smartphone", "5g", "flagship", "camera"),
            badge = "FLAGSHIP",
            isFeatured = true,
            isBestSeller = true
        ),
        Product(
            id = "prod_4",
            name = "All-Weather Commuter Windbreaker Shell",
            slug = "all-weather-commuter-windbreaker-shell",
            brand = "AT Luxe Studio",
            category = "Fashion & Apparel",
            categorySlug = "fashion-apparel",
            sku = "AT-FAS-301",
            price = 145.00,
            originalPrice = 180.00,
            discountPercent = 19,
            stock = 60,
            rating = 4.7,
            reviewCount = 64,
            images = listOf(
                "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800&q=80",
                "https://images.unsplash.com/photo-1544441893-675973e31985?w=800&q=80"
            ),
            thumbnailUrl = "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800&q=80",
            description = "Three-layer Gore-weave membrane with micro-sealed seams. Lightweight, packable, completely windproof and 20,000mm hydrostatic water resistant.",
            specifications = mapOf(
                "Fabric" to "3-Layer Recycled Ripstop Nylon",
                "Waterproof Rating" to "20,000mm Hydrostatic Head",
                "Breathability" to "15,000 g/m²/24h",
                "Zippers" to "YKK AquaGuard Waterproof Zips"
            ),
            features = listOf(
                "Fully taped seams for extreme weather resistance",
                "Storm hood with dual-point cinch cord",
                "Magnetic cuff closures and internal waterproof tech pocket"
            ),
            colors = listOf("Matte Black", "Desert Sand", "Alpine Olive"),
            sizes = listOf("S", "M", "L", "XL"),
            tags = listOf("jacket", "waterproof", "outerwear", "minimalist"),
            badge = "NEW",
            isFeatured = true,
            isNewArrival = true
        ),
        Product(
            id = "prod_5",
            name = "Urban Nomad Minimalist Commuter Backpack 24L",
            slug = "urban-nomad-commuter-backpack-24l",
            brand = "Urban Nomad Co",
            category = "Lifestyle & Gear",
            categorySlug = "lifestyle-gear",
            sku = "AT-LFE-401",
            price = 119.00,
            originalPrice = 149.00,
            discountPercent = 20,
            stock = 34,
            rating = 4.9,
            reviewCount = 89,
            images = listOf(
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80",
                "https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?w=800&q=80"
            ),
            thumbnailUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80",
            description = "Crafted from ballistic Cordura nylon with magnetic Fidlock clasps, a suspended padded 16\" laptop chamber, and hidden RFID-blocking passport compartment.",
            specifications = mapOf(
                "Capacity" to "24 Liters",
                "Material" to "1000D Cordura Ballistic Nylon",
                "Laptop Sleeve" to "Suspended false bottom for up to 16\" MacBooks",
                "Weight" to "1.1 kg",
                "Hardware" to "German Engineered Fidlock V-Buckles"
            ),
            features = listOf(
                "Weatherproof clamshell opening for instant access",
                "Hidden luggage pass-through strap for airport rolling bags",
                "Water-repellent side bottle holster"
            ),
            colors = listOf("Stealth Black", "Carbon Gray", "Navy Blue"),
            sizes = listOf("24L Capacity"),
            tags = listOf("backpack", "commute", "travel", "edc"),
            badge = "SALE",
            isFeatured = true,
            isFlashDeal = true
        ),
        Product(
            id = "prod_6",
            name = "AT Sonic Pods Wireless Earbuds with Spatial Audio",
            slug = "at-sonic-pods-wireless-earbuds",
            brand = "AT Acoustic Labs",
            category = "Audio & Sound",
            categorySlug = "audio-sound",
            sku = "AT-AUD-902",
            price = 129.00,
            originalPrice = 169.00,
            discountPercent = 23,
            stock = 75,
            rating = 4.8,
            reviewCount = 112,
            images = listOf(
                "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800&q=80",
                "https://images.unsplash.com/photo-1606220588913-b3aacb4d2f46?w=800&q=80"
            ),
            thumbnailUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800&q=80",
            description = "True wireless in-ear earbuds with graphene diaphragms, dynamic head-tracking spatial sound, wireless charging case, and IPX7 sweat resistance.",
            specifications = mapOf(
                "Drivers" to "11mm Graphene Coated Drivers",
                "Battery" to "8 Hours buds / 36 Hours with Wireless Case",
                "Waterproof" to "IPX7 Water & Sweatproof",
                "Microphones" to "6-Mic Environmental Noise Cancellation array"
            ),
            features = listOf(
                "Dynamic 3D Spatial Audio with Head Tracking",
                "Qi Wireless & USB-C fast charging case",
                "Transparency Audio Mode for safe street awareness"
            ),
            colors = listOf("Glacier White", "Matte Black", "Sage Green"),
            sizes = listOf("Universal with 4 Ear Tip Sizes"),
            tags = listOf("earbuds", "audio", "wireless", "tws"),
            badge = "POPULAR",
            isFeatured = true,
            isFlashDeal = true
        )
    )

    val sampleCoupons = listOf(
        Coupon(
            code = "ATSHOP40",
            discountPercent = 40,
            minSpend = 100.0,
            maxDiscount = 150.0,
            description = "Summer promotional code: 40% OFF orders over $100"
        ),
        Coupon(
            code = "WELCOME20",
            discountPercent = 20,
            minSpend = 50.0,
            maxDiscount = 60.0,
            description = "Welcome discount: 20% OFF your order"
        ),
        Coupon(
            code = "FREESHIP",
            discountPercent = 0,
            fixedDiscount = 10.0,
            minSpend = 30.0,
            maxDiscount = 10.0,
            description = "Free Shipping voucher on orders over $30"
        )
    )

    val sampleFlashDeal = FlashDeal(
        id = "deal_flash_today",
        title = "Midnight Lightning Flash Sale",
        discountPercent = 25,
        endTimeMillis = System.currentTimeMillis() + (14 * 3600 * 1000L) + (42 * 60 * 1000L), // 14 hours 42 mins from now
        productIds = listOf("prod_1", "prod_5", "prod_6"),
        isActive = true
    )

    val sampleReviews = listOf(
        Review(
            id = "rev_1",
            productId = "prod_1",
            userName = "Marcus Vance",
            rating = 5,
            comment = "The soundstage on these headphones blew me away! Better ANC than my previous $400 pair. The battery life easily lasted my entire 4-day flight itinerary.",
            date = "September 18, 2026",
            isVerifiedPurchase = true
        ),
        Review(
            id = "rev_2",
            productId = "prod_1",
            userName = "Sarah Jenkins",
            rating = 5,
            comment = "Build quality is top tier. Memory foam cups don't pinch even when wearing glasses. Highly recommended for remote work and flights!",
            date = "September 14, 2026",
            isVerifiedPurchase = true
        ),
        Review(
            id = "rev_3",
            productId = "prod_2",
            userName = "David Chen",
            rating = 5,
            comment = "Titanium body is super lightweight. The screen is easily visible in direct noon sunlight during mountain trail runs.",
            date = "September 20, 2026",
            isVerifiedPurchase = true
        )
    )
}
