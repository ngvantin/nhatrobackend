package com.example.nhatrobackend.Entity;

import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId; // ID của phòng

    @Column(nullable = false)
    private Double price; // Giá thuê của phòng

    @Column(nullable = false)
    private Double area; // Diện tích phòng (m2)

    @Enumerated(EnumType.STRING)
    @Column(name = "furniture_status", nullable = false)
    private FurnitureStatus furnitureStatus; // Tình trạng nội thất của phòng

    @Column(name = "number_of_rooms", nullable = false)
    private Integer numberOfRooms; // Số lượng phòng trong một nhà trọ

    @Column(name = "electricity_price", nullable = false)
    private Double electricityPrice; // Giá điện cho phòng

    @Column(name = "water_price", nullable = false)
    private Double waterPrice; // Giá nước cho phòng

    @Column(nullable = false)
    private String city; // Tỉnh/Thành phố

    @Column(nullable = false)
    private String district; // Quận/Huyện

    @Column(nullable = false)
    private String ward; // Phường/Xã

    @Column(nullable = false)
    private String street; // Tên đường

    @Column(name = "house_number", nullable = false)
    private String houseNumber; // Số nhà

    @Column(name = "license_pccc_url", nullable = false)
    private String licensePcccUrl; // Đường dẫn đến giấy chứng nhận PCCC

    @Column(name = "license_business_url", nullable = false)
    private String licenseBusinessUrl; // Đường dẫn đến giấy phép kinh doanh

    // Thiết lập quan hệ 1-1 với Post
    @OneToOne(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private Post post;

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", price=" + price +
                ", area=" + area +
                ", furnitureStatus=" + furnitureStatus +
                ", numberOfRooms=" + numberOfRooms +
                ", electricityPrice=" + electricityPrice +
                ", waterPrice=" + waterPrice +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", ward='" + ward + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", licensePcccUrl='" + licensePcccUrl + '\'' +
                ", licenseBusinessUrl='" + licenseBusinessUrl + '\'' +
                ", post=" + post +
                '}';
    }
}
