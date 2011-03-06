
package eu.helio_vo.xml.queryservice.v0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import net.ivoa.xml.votable.v1.VOTABLE;


/**
 * <p>Java class for queryResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="queryResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ivoa.net/xml/VOTable/v1.1}VOTABLE"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "queryResponse", propOrder = {
    "votable"
})
public class QueryResponse {

    @XmlElement(name = "VOTABLE", namespace = "http://www.ivoa.net/xml/VOTable/v1.1", required = true)
    protected VOTABLE votable;

    /**
     * Gets the value of the votable property.
     * 
     * @return
     *     possible object is
     *     {@link VOTABLE }
     *     
     */
    public VOTABLE getVOTABLE() {
        return votable;
    }

    /**
     * Sets the value of the votable property.
     * 
     * @param value
     *     allowed object is
     *     {@link VOTABLE }
     *     
     */
    public void setVOTABLE(VOTABLE value) {
        this.votable = value;
    }

}
