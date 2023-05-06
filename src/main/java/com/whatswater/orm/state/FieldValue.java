package com.whatswater.orm.state;

import com.whatswater.orm.data.id.DataId;
import com.whatswater.orm.schema.Schema;

import java.util.Objects;

public class FieldValue {
    private Schema schema;
    private String schemaId;
    private String propertyName;
    private DataId id;
    private Object value;
    private boolean dirty;

    public FieldValue(Schema schema, String propertyName, DataId id) {
        this.schema = schema;
        this.propertyName = propertyName;
        this.id = id;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemaId, propertyName, id.getIdValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FieldValue)) {
            return false;
        }
        FieldValue fieldValue = (FieldValue) obj;
        return Objects.equals(this.schemaId, fieldValue.schemaId)
            && Objects.equals(this.propertyName, fieldValue.propertyName)
            && Objects.equals(this.id.getIdValue(), fieldValue.id.getIdValue())
            ;
    }
}
