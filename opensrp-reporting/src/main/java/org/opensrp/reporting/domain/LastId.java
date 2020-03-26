package org.opensrp.reporting.domain;

import javax.persistence.*;

@Entity
@Table(name="last_id")
@NamedQueries({
        @NamedQuery(name = LastId.FIND_LAST_USED_ID_BY_ANM_IDENTIFIER,
        query = "select r from LastId r, ANM a where a.anmIdentifier = :anmIdentifier and r.anm.id = a.id")
})
public class LastId {
    public static final String FIND_LAST_USED_ID_BY_ANM_IDENTIFIER = "find.last.used.id.by.anm.identifier";
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "anm_id", insertable = true, updatable = true)
    @ManyToOne
    private ANM anm;

    @Column(name = "last_id")
    private Long lastId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ANM getAnm() {
        return anm;
    }

    public void setAnm(ANM anm) {
        this.anm = anm;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }
}
