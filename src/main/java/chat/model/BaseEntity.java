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
    @Column(nullable = false, updatable = false, name = "INSERT_DT")
    @JsonIgnore
    private LocalDateTime insertDt;

    @CreatedBy
    @JsonIgnore
    @Column(name = "INSERT_USER_ID")
    private Long insertUserId;

    @LastModifiedDate
    @JsonIgnore
    @Column(name = "UPDATE_DT")
    private LocalDateTime updateDt;

    @LastModifiedBy
    @JsonIgnore
    @Column(name = "UPDATE_USER_ID")
    private Long updateUserId;

    @ColumnDefault("-1")
    @JsonIgnore
    @Column(name = "DELETE_YN")
    protected int deleteYn;

    @JsonIgnore
    @Column(name = "DELETE_DT")
    private LocalDateTime deleteDt;

    @JsonIgnore
    @Column(name = "DELETE_USER_ID")
    private Long deleteUserId;

    @JsonIgnore
    @Column(name = "REMARK")
    private String remark;
}