package chat.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {
    @CreatedDate
    @Column(nullable = false, updatable = false)
    @JsonIgnore
    private LocalDateTime insertDt;

    @CreatedBy
    @JsonIgnore
    private Long insertUserCd;

    @LastModifiedDate
    @JsonIgnore
    private LocalDateTime updateDt;

    @LastModifiedBy
    @JsonIgnore
    private Long updateUserCd;

    @Column(name = "deleteYn")
    @ColumnDefault("-1")
    @JsonIgnore
    protected int deleteYn;

    @JsonIgnore
    private LocalDateTime deleteDt;

    @JsonIgnore
    private Long deleteUserCd;

    @JsonIgnore
    private String remark;
}