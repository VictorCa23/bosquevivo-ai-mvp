package com.solveria.bosquevivo.incidents.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "brigades")
public class Brigade {

    @Id private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 120)
    private String zone;

    @Column(nullable = false)
    private boolean available;

    protected Brigade() {}

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getZone() {
        return zone;
    }

    public boolean isAvailable() {
        return available;
    }
}
