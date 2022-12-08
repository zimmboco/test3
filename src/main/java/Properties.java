import java.time.LocalDateTime;

public class Properties {
    @Property(name = "stringProperty1")
    public String stringProperty;
    public Integer numberProperty;
    public LocalDateTime timeProperty;

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public Integer getNumberProperty() {
        return numberProperty;
    }

    public void setNumberProperty(Integer numberProperty) {
        this.numberProperty = numberProperty;
    }

    public LocalDateTime getTimeProperty() {
        return timeProperty;
    }

    public void setTimeProperty(LocalDateTime timeProperty) {
        this.timeProperty = timeProperty;
    }

    @Override
    public String toString() {
        return "Property{" +
                "stringProperty='" + stringProperty + '\'' +
                ", numberProperty=" + numberProperty +
                ", timeProperty=" + timeProperty +
                '}';
    }
}
