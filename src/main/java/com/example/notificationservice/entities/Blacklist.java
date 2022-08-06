package com.example.notificationservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "blacklist")
public class Blacklist implements Serializable {
    @Id
    private String phoneNumber;
    private boolean isPresent;
}
