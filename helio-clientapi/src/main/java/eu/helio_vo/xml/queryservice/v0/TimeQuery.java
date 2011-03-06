
package eu.helio_vo.xml.queryservice.v0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TimeQuery complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TimeQuery">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="STARTTIME" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ENDTIME" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="FROM" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="MAXRECORDS" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="STARTINDEX" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeQuery", propOrder = {
    "starttime",
    "endtime",
    "from",
    "maxrecords",
    "startindex"
})
public class TimeQuery {

    @XmlElement(name = "STARTTIME")
    protected List<String> starttime;
    @XmlElement(name = "ENDTIME")
    protected List<String> endtime;
    @XmlElement(name = "FROM", required = true)
    protected List<String> from;
    @XmlElement(name = "MAXRECORDS")
    protected Integer maxrecords;
    @XmlElement(name = "STARTINDEX")
    protected Integer startindex;

    /**
     * Gets the value of the starttime property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the starttime property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSTARTTIME().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSTARTTIME() {
        if (starttime == null) {
            starttime = new ArrayList<String>();
        }
        return this.starttime;
    }

    /**
     * Gets the value of the endtime property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the endtime property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getENDTIME().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getENDTIME() {
        if (endtime == null) {
            endtime = new ArrayList<String>();
        }
        return this.endtime;
    }

    /**
     * Gets the value of the from property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the from property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFROM().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFROM() {
        if (from == null) {
            from = new ArrayList<String>();
        }
        return this.from;
    }

    /**
     * Gets the value of the maxrecords property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMAXRECORDS() {
        return maxrecords;
    }

    /**
     * Sets the value of the maxrecords property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMAXRECORDS(Integer value) {
        this.maxrecords = value;
    }

    /**
     * Gets the value of the startindex property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSTARTINDEX() {
        return startindex;
    }

    /**
     * Sets the value of the startindex property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSTARTINDEX(Integer value) {
        this.startindex = value;
    }

}
