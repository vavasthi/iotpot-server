package com.iotpot.server.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tenants",
        uniqueConstraints = {

                @UniqueConstraint(name = "UK_discriminator",  columnNames = "discriminator"),
                @UniqueConstraint(name = "UK_adminEmail", columnNames = "adminEmail")
        }
)
public class TenantEntity extends BaseEntity {

    public TenantEntity(String name,
                        String adminEmail,
                        String discriminator,
                        Set<DataCenterEntity> datacenters) {
        super(name);
        this.adminEmail = adminEmail;
        this.discriminator = discriminator;
        this.datacenters = datacenters;
    }

    public TenantEntity() {
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String email) {
        this.adminEmail = email;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    public Set<DataCenterEntity> getDatacenters() {
        return datacenters;
    }

    public void setDatacenters(Set<DataCenterEntity> computeRegions) {
        this.datacenters = computeRegions;
    }

    public Set<ActorEntity> getActors() {
        return actors;
    }

    public void setActors(Set<ActorEntity> sessionOwners) {
        this.actors = sessionOwners;
    }

    public synchronized DataCenterEntity getNextComputeRegionEntity() {
        DataCenterEntity lowestCre = null;
        for (DataCenterEntity cre : datacenters) {
            if (lowestCre == null || lowestCre.getUserCount() < cre.getUserCount()) {
                lowestCre = cre;
                break;
            }
        }
        if (lowestCre != null) {
            lowestCre.incrementUserCount();
        }
        return lowestCre;
    }

    private String adminEmail;
    private String discriminator;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<DataCenterEntity> datacenters;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "tenant")
    private Set<ActorEntity> actors;

    @OneToMany (fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "tenant")
    private Set<WorkflowEntity> workflows;
}
