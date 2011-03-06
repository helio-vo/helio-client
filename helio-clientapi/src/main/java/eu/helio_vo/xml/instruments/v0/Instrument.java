
package eu.helio_vo.xml.instruments.v0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for instrument.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="instrument">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SMM__GRS"/>
 *     &lt;enumeration value="SMM__HXRBS"/>
 *     &lt;enumeration value="SMM__HXIS"/>
 *     &lt;enumeration value="SMM__BCS"/>
 *     &lt;enumeration value="SMM__FCS"/>
 *     &lt;enumeration value="SMM__UVSP"/>
 *     &lt;enumeration value="SMM__CP"/>
 *     &lt;enumeration value="SMM__ACRIM"/>
 *     &lt;enumeration value="HINOTORI__SXT"/>
 *     &lt;enumeration value="HINOTORI__SOX"/>
 *     &lt;enumeration value="HINOTORI__SGR"/>
 *     &lt;enumeration value="HINOTORI__HXM"/>
 *     &lt;enumeration value="HINOTORI__FLM"/>
 *     &lt;enumeration value="HINOTORI__PXM"/>
 *     &lt;enumeration value="CGRO__BATSE"/>
 *     &lt;enumeration value="YOHKOH__SXT"/>
 *     &lt;enumeration value="YOHKOH__HXT"/>
 *     &lt;enumeration value="YOHKOH__BCS"/>
 *     &lt;enumeration value="YOHKOH__WBS_GRS"/>
 *     &lt;enumeration value="YOHKOH__WBS_HXS"/>
 *     &lt;enumeration value="YOHKOH__WBS_SXS"/>
 *     &lt;enumeration value="SOHO__EIT"/>
 *     &lt;enumeration value="SOHO__CDS"/>
 *     &lt;enumeration value="SOHO__SUMER"/>
 *     &lt;enumeration value="SOHO__UVCS"/>
 *     &lt;enumeration value="SOHO__LASCO"/>
 *     &lt;enumeration value="SOHO__SWAN"/>
 *     &lt;enumeration value="SOHO__MDI"/>
 *     &lt;enumeration value="SOHO__SOI_MDI"/>
 *     &lt;enumeration value="SOHO__GOLF"/>
 *     &lt;enumeration value="SOHO__VIRGO"/>
 *     &lt;enumeration value="SOHO__CELIAS"/>
 *     &lt;enumeration value="SOHO__COSTEP"/>
 *     &lt;enumeration value="SOHO__ERNE"/>
 *     &lt;enumeration value="SOHO__SEM"/>
 *     &lt;enumeration value="TRACE__TRACE_EUV"/>
 *     &lt;enumeration value="TRACE__TRACE_UV"/>
 *     &lt;enumeration value="TRACE__TRACE_VIS"/>
 *     &lt;enumeration value="CORONAS_F__SRT"/>
 *     &lt;enumeration value="CORONAS_F__RES"/>
 *     &lt;enumeration value="CORONAS_F__DIFOS"/>
 *     &lt;enumeration value="CORONAS_F__DIOGENESS"/>
 *     &lt;enumeration value="CORONAS_F__RESIK"/>
 *     &lt;enumeration value="RHESSI__HESSI_GMR"/>
 *     &lt;enumeration value="RHESSI__HESSI_HXR"/>
 *     &lt;enumeration value="CORIOLIS__SMEI"/>
 *     &lt;enumeration value="HINODE__SOT_SP"/>
 *     &lt;enumeration value="HINODE__SOT_FG"/>
 *     &lt;enumeration value="HINODE__XRT"/>
 *     &lt;enumeration value="HINODE__EIS"/>
 *     &lt;enumeration value="STEREO_A__EUVI"/>
 *     &lt;enumeration value="STEREO_A__COR"/>
 *     &lt;enumeration value="STEREO_A__HI"/>
 *     &lt;enumeration value="STEREO_A__SWAVES"/>
 *     &lt;enumeration value="STEREO_A__SWEA"/>
 *     &lt;enumeration value="STEREO_A__MAG"/>
 *     &lt;enumeration value="STEREO_A__PLASTIC"/>
 *     &lt;enumeration value="STEREO_B__EUVI"/>
 *     &lt;enumeration value="STEREO_B__COR"/>
 *     &lt;enumeration value="STEREO_B__HI"/>
 *     &lt;enumeration value="STEREO_B__SWAVES"/>
 *     &lt;enumeration value="STEREO_B__SWEA"/>
 *     &lt;enumeration value="STEREO_B__MAG"/>
 *     &lt;enumeration value="STEREO_B__PLASTIC"/>
 *     &lt;enumeration value="PROBA_2__SWAP"/>
 *     &lt;enumeration value="PROBA_2__LYRA"/>
 *     &lt;enumeration value="PROBA_2__DSLP"/>
 *     &lt;enumeration value="PROBA_2__TPMU"/>
 *     &lt;enumeration value="SDO__HMI"/>
 *     &lt;enumeration value="SDO__AIA_EUV"/>
 *     &lt;enumeration value="SDO__AIA_UV"/>
 *     &lt;enumeration value="SDO__EVE"/>
 *     &lt;enumeration value="GOES__XRS"/>
 *     &lt;enumeration value="GOES__HEPAD"/>
 *     &lt;enumeration value="GOES__EPS"/>
 *     &lt;enumeration value="GOES_12__SXI"/>
 *     &lt;enumeration value="GOES_13__SXI"/>
 *     &lt;enumeration value="ACE__SIS"/>
 *     &lt;enumeration value="ACE__ULEIS"/>
 *     &lt;enumeration value="ACE__EPAM"/>
 *     &lt;enumeration value="ACE__SEPICA"/>
 *     &lt;enumeration value="ACE__SWIMS"/>
 *     &lt;enumeration value="ACE__SWICS"/>
 *     &lt;enumeration value="ACE__SWEPAM"/>
 *     &lt;enumeration value="ACE__MAG"/>
 *     &lt;enumeration value="CLUSTER__PEACE"/>
 *     &lt;enumeration value="CLUSTER__FGM"/>
 *     &lt;enumeration value="CLUSTER__EDI"/>
 *     &lt;enumeration value="CLUSTER__ASPOC"/>
 *     &lt;enumeration value="CLUSTER__STAFF"/>
 *     &lt;enumeration value="CLUSTER__EFW"/>
 *     &lt;enumeration value="CLUSTER__DWP"/>
 *     &lt;enumeration value="CLUSTER__WHISPER"/>
 *     &lt;enumeration value="CLUSTER__WBD"/>
 *     &lt;enumeration value="CLUSTER__CIS"/>
 *     &lt;enumeration value="CLUSTER__RAPID"/>
 *     &lt;enumeration value="ULYSSES__VHM_FGM"/>
 *     &lt;enumeration value="ULYSSES__SWOOPS"/>
 *     &lt;enumeration value="ULYSSES__SWICS"/>
 *     &lt;enumeration value="ULYSSES__URAP"/>
 *     &lt;enumeration value="ULYSSES__HISCALE"/>
 *     &lt;enumeration value="ULYSSES__SCE"/>
 *     &lt;enumeration value="ULYSSES__EPAC"/>
 *     &lt;enumeration value="ULYSSES__COSPIN"/>
 *     &lt;enumeration value="ULYSSES__GRB"/>
 *     &lt;enumeration value="CASSINI__INMS"/>
 *     &lt;enumeration value="CASSINI__RPWS"/>
 *     &lt;enumeration value="CASSINI__CAPS"/>
 *     &lt;enumeration value="CASSINI__MAG"/>
 *     &lt;enumeration value="GALILEO__MAG"/>
 *     &lt;enumeration value="GALILEO__PLS"/>
 *     &lt;enumeration value="GALILEO__EPD"/>
 *     &lt;enumeration value="MGS__MAG_ER"/>
 *     &lt;enumeration value="ODYSSEY__MARIE"/>
 *     &lt;enumeration value="MEX__ASPERA"/>
 *     &lt;enumeration value="ROSETTA__LAP"/>
 *     &lt;enumeration value="ROSETTA__IES"/>
 *     &lt;enumeration value="ROSETTA__MAG"/>
 *     &lt;enumeration value="ROSETTA__ICA"/>
 *     &lt;enumeration value="MESSENGER__XRS"/>
 *     &lt;enumeration value="MESSENGER__EPPS"/>
 *     &lt;enumeration value="MESSENGER__MAG"/>
 *     &lt;enumeration value="VEX__ASPERA"/>
 *     &lt;enumeration value="VEX__MAG"/>
 *     &lt;enumeration value="VOYAGER_1__MAG"/>
 *     &lt;enumeration value="VOYAGER_1__PLS"/>
 *     &lt;enumeration value="VOYAGER_1__LECP"/>
 *     &lt;enumeration value="VOYAGER_1__CRS"/>
 *     &lt;enumeration value="VOYAGER_2__MAG"/>
 *     &lt;enumeration value="VOYAGER_2__PLS"/>
 *     &lt;enumeration value="VOYAGER_2__LECP"/>
 *     &lt;enumeration value="VOYAGER_2__CRS"/>
 *     &lt;enumeration value="MEUD__MWLT"/>
 *     &lt;enumeration value="MEUD__MSH"/>
 *     &lt;enumeration value="NANC__NRH"/>
 *     &lt;enumeration value="NANC__NDA"/>
 *     &lt;enumeration value="NANC__NTRFA"/>
 *     &lt;enumeration value="PDMO__COGHA"/>
 *     &lt;enumeration value="KPNO__MAGMP"/>
 *     &lt;enumeration value="KPNO__SPMAG"/>
 *     &lt;enumeration value="KPNO__VSM"/>
 *     &lt;enumeration value="KPNO__FDP"/>
 *     &lt;enumeration value="KSAC__COGHA"/>
 *     &lt;enumeration value="KSAC__SHELIO"/>
 *     &lt;enumeration value="MLSO__MK3"/>
 *     &lt;enumeration value="MLSO__MK4"/>
 *     &lt;enumeration value="MLSO__DPM"/>
 *     &lt;enumeration value="MLSO__PICS"/>
 *     &lt;enumeration value="MLSO__CHIP"/>
 *     &lt;enumeration value="MWSO__MAGMP"/>
 *     &lt;enumeration value="KISF__HALPH"/>
 *     &lt;enumeration value="THEM__HALPH"/>
 *     &lt;enumeration value="NOBE__NORH"/>
 *     &lt;enumeration value="OVRO__OVSA"/>
 *     &lt;enumeration value="KSFO__CFDT1"/>
 *     &lt;enumeration value="KSFO__CFDT2"/>
 *     &lt;enumeration value="HSOS__HALPH"/>
 *     &lt;enumeration value="YNAO__HALPH"/>
 *     &lt;enumeration value="OACT__HALPH"/>
 *     &lt;enumeration value="KANZ__HALPH"/>
 *     &lt;enumeration value="KANZ__SYNOP"/>
 *     &lt;enumeration value="BBSO__SYNOP"/>
 *     &lt;enumeration value="CUCS__HASTA"/>
 *     &lt;enumeration value="LEAR__GONG_SYN"/>
 *     &lt;enumeration value="UDPR__GONG_SYN"/>
 *     &lt;enumeration value="TEID__GONG_SYN"/>
 *     &lt;enumeration value="CTIO__GONG_SYN"/>
 *     &lt;enumeration value="BBSO__GONG_SYN"/>
 *     &lt;enumeration value="MLSO__GONG_SYN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "instrument", namespace = "http://helio-vo.eu/xml/Instruments/v0.1")
@XmlEnum
public enum Instrument {


    /**
     * 
     *               Gamma-Ray Spectrometer
     *              
     * 
     */
    @XmlEnumValue("SMM__GRS")
    SMM_GRS("SMM__GRS"),

    /**
     * 
     *              Hard X-ray Burst Spectrometer
     *              
     * 
     */
    @XmlEnumValue("SMM__HXRBS")
    SMM_HXRBS("SMM__HXRBS"),

    /**
     * 
     *             Hard X-ray Imaging Spectrometer
     *              
     * 
     */
    @XmlEnumValue("SMM__HXIS")
    SMM_HXIS("SMM__HXIS"),

    /**
     * 
     *              Bent Crystal Spectrometer  (XRP)
     *              
     * 
     */
    @XmlEnumValue("SMM__BCS")
    SMM_BCS("SMM__BCS"),

    /**
     * 
     *          Flat Crystal Spectrometer  (XRP)
     *              
     * 
     */
    @XmlEnumValue("SMM__FCS")
    SMM_FCS("SMM__FCS"),

    /**
     * 
     *      Ultra-Violet Spectrometer + Polarimeter
     *              
     * 
     */
    @XmlEnumValue("SMM__UVSP")
    SMM_UVSP("SMM__UVSP"),

    /**
     * 
     *     Coronagraph Polarimeter
     *              
     * 
     */
    @XmlEnumValue("SMM__CP")
    SMM_CP("SMM__CP"),

    /**
     * 
     *    Active Cavity Radiometer
     *              
     * 
     */
    @XmlEnumValue("SMM__ACRIM")
    SMM_ACRIM("SMM__ACRIM"),

    /**
     * 
     *      Solar X-ray Telescope
     *              
     * 
     */
    @XmlEnumValue("HINOTORI__SXT")
    HINOTORI_SXT("HINOTORI__SXT"),

    /**
     * 
     *     Soft X-ray crystal spectrometer
     *              
     * 
     */
    @XmlEnumValue("HINOTORI__SOX")
    HINOTORI_SOX("HINOTORI__SOX"),

    /**
     * 
     *      Solar Gamma-Ray detector
     *              
     * 
     */
    @XmlEnumValue("HINOTORI__SGR")
    HINOTORI_SGR("HINOTORI__SGR"),

    /**
     * 
     *       Hard X-ray Monitor
     *              
     * 
     */
    @XmlEnumValue("HINOTORI__HXM")
    HINOTORI_HXM("HINOTORI__HXM"),

    /**
     * 
     *      Soft X-ray Monitor
     *              
     * 
     */
    @XmlEnumValue("HINOTORI__FLM")
    HINOTORI_FLM("HINOTORI__FLM"),

    /**
     * 
     *         Particle Detector
     *              
     * 
     */
    @XmlEnumValue("HINOTORI__PXM")
    HINOTORI_PXM("HINOTORI__PXM"),

    /**
     * 
     *        Burst And Transient Source Experiment
     *              
     * 
     */
    @XmlEnumValue("CGRO__BATSE")
    CGRO_BATSE("CGRO__BATSE"),

    /**
     * 
     *        Soft X-ray Telescope
     *              
     * 
     */
    @XmlEnumValue("YOHKOH__SXT")
    YOHKOH_SXT("YOHKOH__SXT"),

    /**
     * 
     *       Hard X-ray Telescope
     *              
     * 
     */
    @XmlEnumValue("YOHKOH__HXT")
    YOHKOH_HXT("YOHKOH__HXT"),

    /**
     * 
     *      Bragg Crystal Spectrometer
     *              
     * 
     */
    @XmlEnumValue("YOHKOH__BCS")
    YOHKOH_BCS("YOHKOH__BCS"),

    /**
     * 
     *       Gamma-Ray Spectrometer  (WBS)
     *              
     * 
     */
    @XmlEnumValue("YOHKOH__WBS_GRS")
    YOHKOH_WBS_GRS("YOHKOH__WBS_GRS"),

    /**
     * 
     *         Hard X-ray Spectrometer  (WBS)
     *              
     * 
     */
    @XmlEnumValue("YOHKOH__WBS_HXS")
    YOHKOH_WBS_HXS("YOHKOH__WBS_HXS"),

    /**
     * 
     *        Soft X-ray Spectrometer  (WBS)
     *              
     * 
     */
    @XmlEnumValue("YOHKOH__WBS_SXS")
    YOHKOH_WBS_SXS("YOHKOH__WBS_SXS"),

    /**
     * 
     *         Extreme-ultraviolet Imaging Telescope
     *              
     * 
     */
    @XmlEnumValue("SOHO__EIT")
    SOHO_EIT("SOHO__EIT"),

    /**
     * 
     *         Coronal Diagnostic Spectrometer
     *              
     * 
     */
    @XmlEnumValue("SOHO__CDS")
    SOHO_CDS("SOHO__CDS"),

    /**
     * 
     *       Solar Ultraviolet Measurements of Emitted Radiation 
     *              
     * 
     */
    @XmlEnumValue("SOHO__SUMER")
    SOHO_SUMER("SOHO__SUMER"),

    /**
     * 
     *       UltraViolet Coronagraphic Spectrometer
     *              
     * 
     */
    @XmlEnumValue("SOHO__UVCS")
    SOHO_UVCS("SOHO__UVCS"),

    /**
     * 
     *      Large Angle Spectroscopic Coronagraph
     *              
     * 
     */
    @XmlEnumValue("SOHO__LASCO")
    SOHO_LASCO("SOHO__LASCO"),

    /**
     * 
     *       Solar Wind ANisotropies
     *              
     * 
     */
    @XmlEnumValue("SOHO__SWAN")
    SOHO_SWAN("SOHO__SWAN"),

    /**
     * 
     *      Michelson Doppler Imager
     *              
     * 
     */
    @XmlEnumValue("SOHO__MDI")
    SOHO_MDI("SOHO__MDI"),

    /**
     * 
     *     Solar Oscillation Investigation - Michelson Doppler Imager
     *              
     * 
     */
    @XmlEnumValue("SOHO__SOI_MDI")
    SOHO_SOI_MDI("SOHO__SOI_MDI"),

    /**
     * 
     *      Global Oscillations at Low Frequency
     *              
     * 
     */
    @XmlEnumValue("SOHO__GOLF")
    SOHO_GOLF("SOHO__GOLF"),

    /**
     * 
     *       Variability of solar IRradiance and Gravity Oscillations
     *              
     * 
     */
    @XmlEnumValue("SOHO__VIRGO")
    SOHO_VIRGO("SOHO__VIRGO"),

    /**
     * 
     *        Charge, Element and Isotope Analysis System
     *              
     * 
     */
    @XmlEnumValue("SOHO__CELIAS")
    SOHO_CELIAS("SOHO__CELIAS"),

    /**
     * 
     *         Comprehensive Suprathermal and Energetic Particle Analyzer
     *              
     * 
     */
    @XmlEnumValue("SOHO__COSTEP")
    SOHO_COSTEP("SOHO__COSTEP"),

    /**
     * 
     *        Energetic Particle and Relative Nuclei and Electron Experiment
     *              
     * 
     */
    @XmlEnumValue("SOHO__ERNE")
    SOHO_ERNE("SOHO__ERNE"),

    /**
     * 
     *       Solar Extreme ultraviolet Monitor
     *              
     * 
     */
    @XmlEnumValue("SOHO__SEM")
    SOHO_SEM("SOHO__SEM"),

    /**
     * 
     *      Transition Region and Coronal Explorer
     *              
     * 
     */
    @XmlEnumValue("TRACE__TRACE_EUV")
    TRACE_TRACE_EUV("TRACE__TRACE_EUV"),

    /**
     * 
     *      Transition Region and Coronal Explorer
     *              
     * 
     */
    @XmlEnumValue("TRACE__TRACE_UV")
    TRACE_TRACE_UV("TRACE__TRACE_UV"),

    /**
     * 
     *      Transition Region and Coronal Explorer
     *              
     * 
     */
    @XmlEnumValue("TRACE__TRACE_VIS")
    TRACE_TRACE_VIS("TRACE__TRACE_VIS"),

    /**
     * 
     *     Soft X-ray telescope  (SPIRIT)
     *              
     * 
     */
    @XmlEnumValue("CORONAS_F__SRT")
    CORONAS_F_SRT("CORONAS_F__SRT"),

    /**
     * 
     *     X-ray Spectroheliograph  (SPIRIT)
     *              
     * 
     */
    @XmlEnumValue("CORONAS_F__RES")
    CORONAS_F_RES("CORONAS_F__RES"),

    /**
     * 
     *     Multichannel solar photometer
     *              
     * 
     */
    @XmlEnumValue("CORONAS_F__DIFOS")
    CORONAS_F_DIFOS("CORONAS_F__DIFOS"),

    /**
     * 
     *     X-ray Spectrometer and Photometer
     *              
     * 
     */
    @XmlEnumValue("CORONAS_F__DIOGENESS")
    CORONAS_F_DIOGENESS("CORONAS_F__DIOGENESS"),

    /**
     * 
     *     X-ray Spectrometer
     *              
     * 
     */
    @XmlEnumValue("CORONAS_F__RESIK")
    CORONAS_F_RESIK("CORONAS_F__RESIK"),

    /**
     * 
     *   Ramaty High Energy Solar Spectroscopic Imager
     *              
     * 
     */
    @XmlEnumValue("RHESSI__HESSI_GMR")
    RHESSI_HESSI_GMR("RHESSI__HESSI_GMR"),

    /**
     * 
     *      Ramaty High Energy Solar Spectroscopic Imager
     *              
     * 
     */
    @XmlEnumValue("RHESSI__HESSI_HXR")
    RHESSI_HESSI_HXR("RHESSI__HESSI_HXR"),

    /**
     * 
     *      Solar Mass Ejection Imager
     *              
     * 
     */
    @XmlEnumValue("CORIOLIS__SMEI")
    CORIOLIS_SMEI("CORIOLIS__SMEI"),

    /**
     * 
     *     Solar Optical Telescope - Spectro-polarimeter
     *              
     * 
     */
    @XmlEnumValue("HINODE__SOT_SP")
    HINODE_SOT_SP("HINODE__SOT_SP"),

    /**
     * 
     *     Solar Optical Telescope - Filter vector-magnetogram
     *              
     * 
     */
    @XmlEnumValue("HINODE__SOT_FG")
    HINODE_SOT_FG("HINODE__SOT_FG"),

    /**
     * 
     *  X-Ray Telescope
     *              
     * 
     */
    @XmlEnumValue("HINODE__XRT")
    HINODE_XRT("HINODE__XRT"),

    /**
     * 
     *   EUV Imaging Spectrograph
     *              
     * 
     */
    @XmlEnumValue("HINODE__EIS")
    HINODE_EIS("HINODE__EIS"),

    /**
     * 
     *   Extreme UltraViolet Imager  (SECCHI)
     *              
     * 
     */
    @XmlEnumValue("STEREO_A__EUVI")
    STEREO_A_EUVI("STEREO_A__EUVI"),

    /**
     * 
     *    White light Coronagraph 1 and 2  (SECCHI)
     *              
     * 
     */
    @XmlEnumValue("STEREO_A__COR")
    STEREO_A_COR("STEREO_A__COR"),

    /**
     * 
     *     Heliospheric Imager 1 and 2  (SECCHI)
     *              
     * 
     */
    @XmlEnumValue("STEREO_A__HI")
    STEREO_A_HI("STEREO_A__HI"),

    /**
     * 
     *       STEREO-WAVES
     *              
     * 
     */
    @XmlEnumValue("STEREO_A__SWAVES")
    STEREO_A_SWAVES("STEREO_A__SWAVES"),

    /**
     * 
     *       ? (IMPACT)
     *              
     * 
     */
    @XmlEnumValue("STEREO_A__SWEA")
    STEREO_A_SWEA("STEREO_A__SWEA"),

    /**
     * 
     *       Magnetometer (IMPACT)
     *              
     * 
     */
    @XmlEnumValue("STEREO_A__MAG")
    STEREO_A_MAG("STEREO_A__MAG"),

    /**
     * 
     *        PLasma and SupraThermal Ion and Composition
     *              
     * 
     */
    @XmlEnumValue("STEREO_A__PLASTIC")
    STEREO_A_PLASTIC("STEREO_A__PLASTIC"),

    /**
     * 
     *       Extreme UltraViolet Imager  (SECCHI)
     *              
     * 
     */
    @XmlEnumValue("STEREO_B__EUVI")
    STEREO_B_EUVI("STEREO_B__EUVI"),

    /**
     * 
     *       White light Coronagraph 1 and 2  (SECCHI)
     *              
     * 
     */
    @XmlEnumValue("STEREO_B__COR")
    STEREO_B_COR("STEREO_B__COR"),

    /**
     * 
     *       Heliospheric Imager 1 and 2  (SECCHI)
     *              
     * 
     */
    @XmlEnumValue("STEREO_B__HI")
    STEREO_B_HI("STEREO_B__HI"),

    /**
     * 
     *       STEREO-WAVES
     *              
     * 
     */
    @XmlEnumValue("STEREO_B__SWAVES")
    STEREO_B_SWAVES("STEREO_B__SWAVES"),

    /**
     * 
     *          ? (IMPACT)
     *              
     * 
     */
    @XmlEnumValue("STEREO_B__SWEA")
    STEREO_B_SWEA("STEREO_B__SWEA"),

    /**
     * 
     *  Magnetometer (IMPACT)
     *              
     * 
     */
    @XmlEnumValue("STEREO_B__MAG")
    STEREO_B_MAG("STEREO_B__MAG"),

    /**
     * 
     *   PLasma and SupraThermal Ion and Composition
     *              
     * 
     */
    @XmlEnumValue("STEREO_B__PLASTIC")
    STEREO_B_PLASTIC("STEREO_B__PLASTIC"),

    /**
     * 
     *   Sun Watcher using AP-sensors and image Processing experiment
     *              
     * 
     */
    @XmlEnumValue("PROBA_2__SWAP")
    PROBA_2_SWAP("PROBA_2__SWAP"),

    /**
     * 
     *    Lyman-Alpha radiometer
     *              
     * 
     */
    @XmlEnumValue("PROBA_2__LYRA")
    PROBA_2_LYRA("PROBA_2__LYRA"),

    /**
     * 
     *  Dual Segmented Langmuir Probes
     *              
     * 
     */
    @XmlEnumValue("PROBA_2__DSLP")
    PROBA_2_DSLP("PROBA_2__DSLP"),

    /**
     * 
     *  Thermal plasma measurement unit
     *              
     * 
     */
    @XmlEnumValue("PROBA_2__TPMU")
    PROBA_2_TPMU("PROBA_2__TPMU"),

    /**
     * 
     *   Heliospheric and Magnetic Imager
     *              
     * 
     */
    @XmlEnumValue("SDO__HMI")
    SDO_HMI("SDO__HMI"),

    /**
     * 
     *  Atmospheric Imaging Assembly
     *              
     * 
     */
    @XmlEnumValue("SDO__AIA_EUV")
    SDO_AIA_EUV("SDO__AIA_EUV"),

    /**
     * 
     *  Atmospheric Imaging Assembly
     *              
     * 
     */
    @XmlEnumValue("SDO__AIA_UV")
    SDO_AIA_UV("SDO__AIA_UV"),

    /**
     * 
     *  Extreme ultraviolet Variability Experiment
     *              
     * 
     */
    @XmlEnumValue("SDO__EVE")
    SDO_EVE("SDO__EVE"),

    /**
     * 
     *  X-ray Sensor
     *              
     * 
     */
    @XmlEnumValue("GOES__XRS")
    GOES_XRS("GOES__XRS"),

    /**
     * 
     *  High Energy Proton and Alpha Detector
     *              
     * 
     */
    @XmlEnumValue("GOES__HEPAD")
    GOES_HEPAD("GOES__HEPAD"),

    /**
     * 
     *    Energetic Particle Sensor
     *              
     * 
     */
    @XmlEnumValue("GOES__EPS")
    GOES_EPS("GOES__EPS"),

    /**
     * 
     *    Soft X-ray Imager
     *              
     * 
     */
    @XmlEnumValue("GOES_12__SXI")
    GOES_12_SXI("GOES_12__SXI"),

    /**
     * 
     *  Soft X-ray Imager
     *              
     * 
     */
    @XmlEnumValue("GOES_13__SXI")
    GOES_13_SXI("GOES_13__SXI"),

    /**
     * 
     *  Solar Isotope Spectrometer
     *              
     * 
     */
    @XmlEnumValue("ACE__SIS")
    ACE_SIS("ACE__SIS"),

    /**
     * 
     *   Ulltra Low Energy Isotope Spectometer
     *              
     * 
     */
    @XmlEnumValue("ACE__ULEIS")
    ACE_ULEIS("ACE__ULEIS"),

    /**
     * 
     * Electron, Proton and Alpha Monitor
     *              
     * 
     */
    @XmlEnumValue("ACE__EPAM")
    ACE_EPAM("ACE__EPAM"),

    /**
     * 
     * Solar Energetic Particle Ionic Charge Analyzer
     *              
     * 
     */
    @XmlEnumValue("ACE__SEPICA")
    ACE_SEPICA("ACE__SEPICA"),

    /**
     * 
     * Solar Wind Ion Mass Spectrometer
     *              
     * 
     */
    @XmlEnumValue("ACE__SWIMS")
    ACE_SWIMS("ACE__SWIMS"),

    /**
     * 
     * Solar Wind Ion Composition Spectrometer
     *              
     * 
     */
    @XmlEnumValue("ACE__SWICS")
    ACE_SWICS("ACE__SWICS"),

    /**
     * 
     * Solar Wind Electron, Proton and Alpha Monitor
     *              
     * 
     */
    @XmlEnumValue("ACE__SWEPAM")
    ACE_SWEPAM("ACE__SWEPAM"),

    /**
     * 
     *   Magnetic Field Experiment
     *              
     * 
     */
    @XmlEnumValue("ACE__MAG")
    ACE_MAG("ACE__MAG"),

    /**
     * 
     *  Plasma Electron And Current Experiment
     *              
     * 
     */
    @XmlEnumValue("CLUSTER__PEACE")
    CLUSTER_PEACE("CLUSTER__PEACE"),

    /**
     * 
     * Fluxgate Magnetometer
     *              
     * 
     */
    @XmlEnumValue("CLUSTER__FGM")
    CLUSTER_FGM("CLUSTER__FGM"),

    /**
     * 
     * Electron Drift Instrument
     *              
     * 
     */
    @XmlEnumValue("CLUSTER__EDI")
    CLUSTER_EDI("CLUSTER__EDI"),

    /**
     * 
     *   Active Spacecraft Potential Control experiment
     *              
     * 
     */
    @XmlEnumValue("CLUSTER__ASPOC")
    CLUSTER_ASPOC("CLUSTER__ASPOC"),

    /**
     * 
     * Spatio-Temporal Analysis of Field Fluctuation experiment
     *              
     * 
     */
    @XmlEnumValue("CLUSTER__STAFF")
    CLUSTER_STAFF("CLUSTER__STAFF"),

    /**
     * 
     * Electric Field and Wave experiment
     *              
     * 
     */
    @XmlEnumValue("CLUSTER__EFW")
    CLUSTER_EFW("CLUSTER__EFW"),

    /**
     * 
     * Digital Wave Processing experiment
     *              
     * 
     */
    @XmlEnumValue("CLUSTER__DWP")
    CLUSTER_DWP("CLUSTER__DWP"),

    /**
     * 
     *   Waves of HIgh frequency and Sounder for Probing of Electron density by Relaxation
     *              
     * 
     */
    @XmlEnumValue("CLUSTER__WHISPER")
    CLUSTER_WHISPER("CLUSTER__WHISPER"),

    /**
     * 
     *   Wide Band Data instrument
     *              
     * 
     */
    @XmlEnumValue("CLUSTER__WBD")
    CLUSTER_WBD("CLUSTER__WBD"),

    /**
     * 
     *   Cluster Ion Spectrometer experiment
     *              
     * 
     */
    @XmlEnumValue("CLUSTER__CIS")
    CLUSTER_CIS("CLUSTER__CIS"),

    /**
     * 
     *  Research with Adaptive Particle Imaging Detector
     *              
     * 
     */
    @XmlEnumValue("CLUSTER__RAPID")
    CLUSTER_RAPID("CLUSTER__RAPID"),

    /**
     * 
     * Magnetometer
     *              
     * 
     */
    @XmlEnumValue("ULYSSES__VHM_FGM")
    ULYSSES_VHM_FGM("ULYSSES__VHM_FGM"),

    /**
     * 
     *  Solar Wind Plasma Experiment
     *              
     * 
     */
    @XmlEnumValue("ULYSSES__SWOOPS")
    ULYSSES_SWOOPS("ULYSSES__SWOOPS"),

    /**
     * 
     *  Solar Wind Ion Composition Spectrometer
     *              
     * 
     */
    @XmlEnumValue("ULYSSES__SWICS")
    ULYSSES_SWICS("ULYSSES__SWICS"),

    /**
     * 
     * Unified Radio and Plasma Wave Investigation
     *              
     * 
     */
    @XmlEnumValue("ULYSSES__URAP")
    ULYSSES_URAP("ULYSSES__URAP"),

    /**
     * 
     *   Heliospheric Instrument for Spectra, Composition and Anisotropy at Low Energy
     *              
     * 
     */
    @XmlEnumValue("ULYSSES__HISCALE")
    ULYSSES_HISCALE("ULYSSES__HISCALE"),

    /**
     * 
     *   Coronal-Sounding Experiment
     *              
     * 
     */
    @XmlEnumValue("ULYSSES__SCE")
    ULYSSES_SCE("ULYSSES__SCE"),

    /**
     * 
     *  Energetic Particle Composition Experiment
     *              
     * 
     */
    @XmlEnumValue("ULYSSES__EPAC")
    ULYSSES_EPAC("ULYSSES__EPAC"),

    /**
     * 
     *   Cosmic-ray and Solar Particle Investigation
     *              
     * 
     */
    @XmlEnumValue("ULYSSES__COSPIN")
    ULYSSES_COSPIN("ULYSSES__COSPIN"),

    /**
     * 
     * Solar X-ray and Cosmic Gamma-Ray Burst Instrument
     *              
     * 
     */
    @XmlEnumValue("ULYSSES__GRB")
    ULYSSES_GRB("ULYSSES__GRB"),

    /**
     * 
     *    Ion and Neutral Mass Spectrometer
     *              
     * 
     */
    @XmlEnumValue("CASSINI__INMS")
    CASSINI_INMS("CASSINI__INMS"),

    /**
     * 
     *  Radio and Plasma Wave Spectrometer
     *              
     * 
     */
    @XmlEnumValue("CASSINI__RPWS")
    CASSINI_RPWS("CASSINI__RPWS"),

    /**
     * 
     *  Cassini Plasma Spectrometer
     *              
     * 
     */
    @XmlEnumValue("CASSINI__CAPS")
    CASSINI_CAPS("CASSINI__CAPS"),

    /**
     * 
     *   Dual Technique Magnetometer
     *              
     * 
     */
    @XmlEnumValue("CASSINI__MAG")
    CASSINI_MAG("CASSINI__MAG"),

    /**
     * 
     *    Magnetometer (MAG) 
     *              
     * 
     */
    @XmlEnumValue("GALILEO__MAG")
    GALILEO_MAG("GALILEO__MAG"),

    /**
     * 
     *      Plasma Detector (PLS)  
     *              
     * 
     */
    @XmlEnumValue("GALILEO__PLS")
    GALILEO_PLS("GALILEO__PLS"),

    /**
     * 
     *               Energetic Particles Detector (EPD) 
     *              
     * 
     */
    @XmlEnumValue("GALILEO__EPD")
    GALILEO_EPD("GALILEO__EPD"),

    /**
     * 
     *  Magnetometer-Electron Reflectometer (MAG-ER) 
     *              
     * 
     */
    @XmlEnumValue("MGS__MAG_ER")
    MGS_MAG_ER("MGS__MAG_ER"),

    /**
     * 
     *  Mars Radiation Environment Experiment (MARIE) 
     *              
     * 
     */
    @XmlEnumValue("ODYSSEY__MARIE")
    ODYSSEY_MARIE("ODYSSEY__MARIE"),

    /**
     * 
     * Analyzer of Space Plasmas and Energetic Atoms (ASPERA) 
     *              
     * 
     */
    @XmlEnumValue("MEX__ASPERA")
    MEX_ASPERA("MEX__ASPERA"),

    /**
     * 
     *  Langmuir Probe (LAP) 
     *              
     * 
     */
    @XmlEnumValue("ROSETTA__LAP")
    ROSETTA_LAP("ROSETTA__LAP"),

    /**
     * 
     *     Ion and Electron Sensor (IES) 
     *              
     * 
     */
    @XmlEnumValue("ROSETTA__IES")
    ROSETTA_IES("ROSETTA__IES"),

    /**
     * 
     *   Fluxgate Magnetometer (MAG) 
     *              
     * 
     */
    @XmlEnumValue("ROSETTA__MAG")
    ROSETTA_MAG("ROSETTA__MAG"),

    /**
     * 
     *  Ion Composition Analyser (ICA) 
     *              
     * 
     */
    @XmlEnumValue("ROSETTA__ICA")
    ROSETTA_ICA("ROSETTA__ICA"),

    /**
     * 
     *  X-ray Spectrometer (XRS) 
     *              
     * 
     */
    @XmlEnumValue("MESSENGER__XRS")
    MESSENGER_XRS("MESSENGER__XRS"),

    /**
     * 
     *   Energetic Particle and Plasma Spectrometer (EPPS) 
     *              
     * 
     */
    @XmlEnumValue("MESSENGER__EPPS")
    MESSENGER_EPPS("MESSENGER__EPPS"),

    /**
     * 
     *  Magnetometer (MAG) 
     *              
     * 
     */
    @XmlEnumValue("MESSENGER__MAG")
    MESSENGER_MAG("MESSENGER__MAG"),

    /**
     * 
     *     Analyzer of Space Plasmas and Energetic Atoms (ASPERA-4) 
     *              
     * 
     */
    @XmlEnumValue("VEX__ASPERA")
    VEX_ASPERA("VEX__ASPERA"),

    /**
     * 
     *   Magnetometer (MAG) 
     *              
     * 
     */
    @XmlEnumValue("VEX__MAG")
    VEX_MAG("VEX__MAG"),

    /**
     * 
     * Triaxial Fluxgate Magnetometer (MAG) 
     *              
     * 
     */
    @XmlEnumValue("VOYAGER_1__MAG")
    VOYAGER_1_MAG("VOYAGER_1__MAG"),

    /**
     * 
     *   Plasma Spectrometer (PLS) 
     *              
     * 
     */
    @XmlEnumValue("VOYAGER_1__PLS")
    VOYAGER_1_PLS("VOYAGER_1__PLS"),

    /**
     * 
     *   Low-Energy Charged Particles (LECP) 
     *              
     * 
     */
    @XmlEnumValue("VOYAGER_1__LECP")
    VOYAGER_1_LECP("VOYAGER_1__LECP"),

    /**
     * 
     * Cosmic Ray System (CRS) 
     *              
     * 
     */
    @XmlEnumValue("VOYAGER_1__CRS")
    VOYAGER_1_CRS("VOYAGER_1__CRS"),

    /**
     * 
     *  Triaxial Fluxgate Magnetometer (MAG) 
     *              
     * 
     */
    @XmlEnumValue("VOYAGER_2__MAG")
    VOYAGER_2_MAG("VOYAGER_2__MAG"),

    /**
     * 
     *  Plasma Spectrometer (PLS) 
     *              
     * 
     */
    @XmlEnumValue("VOYAGER_2__PLS")
    VOYAGER_2_PLS("VOYAGER_2__PLS"),

    /**
     * 
     *          Low-Energy Charged Particles (LECP) 
     *              
     * 
     */
    @XmlEnumValue("VOYAGER_2__LECP")
    VOYAGER_2_LECP("VOYAGER_2__LECP"),

    /**
     * 
     *  Cosmic Ray System (CRS) 
     *              
     * 
     */
    @XmlEnumValue("VOYAGER_2__CRS")
    VOYAGER_2_CRS("VOYAGER_2__CRS"),

    /**
     * 
     *   Meudon White Light Telescope
     *              
     * 
     */
    @XmlEnumValue("MEUD__MWLT")
    MEUD_MWLT("MEUD__MWLT"),

    /**
     * 
     *   Meudon Spectroheliograph 
     *              
     * 
     */
    @XmlEnumValue("MEUD__MSH")
    MEUD_MSH("MEUD__MSH"),

    /**
     * 
     *  Nancay Radioheliograph  (164 + 327 MHz) 
     *              
     * 
     */
    @XmlEnumValue("NANC__NRH")
    NANC_NRH("NANC__NRH"),

    /**
     * 
     *  Nancay Decametric Array
     *              
     * 
     */
    @XmlEnumValue("NANC__NDA")
    NANC_NDA("NANC__NDA"),

    /**
     * 
     *   Nancay Total Radio Flux Antenna
     *              
     * 
     */
    @XmlEnumValue("NANC__NTRFA")
    NANC_NTRFA("NANC__NTRFA"),

    /**
     * 
     *    Pic-du-Midi H-alpha Coronagraph
     *              
     * 
     */
    @XmlEnumValue("PDMO__COGHA")
    PDMO_COGHA("PDMO__COGHA"),

    /**
     * 
     *   KPVT 512 channel Magnetograph
     *              
     * 
     */
    @XmlEnumValue("KPNO__MAGMP")
    KPNO_MAGMP("KPNO__MAGMP"),

    /**
     * 
     *  KPVT Spectro-Magnetograph
     *              
     * 
     */
    @XmlEnumValue("KPNO__SPMAG")
    KPNO_SPMAG("KPNO__SPMAG"),

    /**
     * 
     * SOLIS Vector Spectro-Magnetograph
     *              
     * 
     */
    @XmlEnumValue("KPNO__VSM")
    KPNO_VSM("KPNO__VSM"),

    /**
     * 
     * SOLIS Full Disk Patrol
     *              
     * 
     */
    @XmlEnumValue("KPNO__FDP")
    KPNO_FDP("KPNO__FDP"),

    /**
     * 
     *  Evans Coronagraph
     *              
     * 
     */
    @XmlEnumValue("KSAC__COGHA")
    KSAC_COGHA("KSAC__COGHA"),

    /**
     * 
     *     Evans Spectroheliograph
     *              
     * 
     */
    @XmlEnumValue("KSAC__SHELIO")
    KSAC_SHELIO("KSAC__SHELIO"),

    /**
     * 
     * Coronagraph, Mk III
     *              
     * 
     */
    @XmlEnumValue("MLSO__MK3")
    MLSO_MK_3("MLSO__MK3"),

    /**
     * 
     *  Coronagraph, Mk IV
     *              
     * 
     */
    @XmlEnumValue("MLSO__MK4")
    MLSO_MK_4("MLSO__MK4"),

    /**
     * 
     *  Digital Prominence Monitor
     *              
     * 
     */
    @XmlEnumValue("MLSO__DPM")
    MLSO_DPM("MLSO__DPM"),

    /**
     * 
     * Polarimeter for Inner Coronal Studies
     *              
     * 
     */
    @XmlEnumValue("MLSO__PICS")
    MLSO_PICS("MLSO__PICS"),

    /**
     * 
     *  Chromospheric Helium Imaging Photometer
     *              
     * 
     */
    @XmlEnumValue("MLSO__CHIP")
    MLSO_CHIP("MLSO__CHIP"),

    /**
     * 
     * Magnetograph
     *              
     * 
     */
    @XmlEnumValue("MWSO__MAGMP")
    MWSO_MAGMP("MWSO__MAGMP"),

    /**
     * 
     * H-Alpha Telescope  (Patrol)
     *              
     * 
     */
    @XmlEnumValue("KISF__HALPH")
    KISF_HALPH("KISF__HALPH"),

    /**
     * 
     *     H-Alpha Telescope??
     *              
     * 
     */
    @XmlEnumValue("THEM__HALPH")
    THEM_HALPH("THEM__HALPH"),

    /**
     * 
     *    Nobeyama Radioheliograph  (17 + 34 GHz)
     *              
     * 
     */
    @XmlEnumValue("NOBE__NORH")
    NOBE_NORH("NOBE__NORH"),

    /**
     * 
     *  Owens Valley Solar Array  (radioheliograph, 1-18 GHz)
     *              
     * 
     */
    @XmlEnumValue("OVRO__OVSA")
    OVRO_OVSA("OVRO__OVSA"),

    /**
     * 
     * Cartesian Full Disk Telecsope No. 1
     *              
     * 
     */
    @XmlEnumValue("KSFO__CFDT1")
    KSFO_CFDT_1("KSFO__CFDT1"),

    /**
     * 
     * Cartesian Full Disk Telecsope No. 2
     *              
     * 
     */
    @XmlEnumValue("KSFO__CFDT2")
    KSFO_CFDT_2("KSFO__CFDT2"),

    /**
     * 
     *   H-Alpha Telescope  (Global H-alpha Network)
     *              
     * 
     */
    @XmlEnumValue("HSOS__HALPH")
    HSOS_HALPH("HSOS__HALPH"),

    /**
     * 
     * H-Alpha Telescope  (Global H-alpha Network)
     *              
     * 
     */
    @XmlEnumValue("YNAO__HALPH")
    YNAO_HALPH("YNAO__HALPH"),

    /**
     * 
     *  H-Alpha Telescope  (Global H-alpha Network)
     *              
     * 
     */
    @XmlEnumValue("OACT__HALPH")
    OACT_HALPH("OACT__HALPH"),

    /**
     * 
     *  H-Alpha Telescope  (Global H-alpha Network)
     *              
     * 
     */
    @XmlEnumValue("KANZ__HALPH")
    KANZ_HALPH("KANZ__HALPH"),

    /**
     * 
     * H-Alpha Telescope  (Global H-alpha Network)
     *              
     * 
     */
    @XmlEnumValue("KANZ__SYNOP")
    KANZ_SYNOP("KANZ__SYNOP"),

    /**
     * 
     *  H-Alpha Telescope  (Synoptic + Global H-alpha Network)
     *              
     * 
     */
    @XmlEnumValue("BBSO__SYNOP")
    BBSO_SYNOP("BBSO__SYNOP"),

    /**
     * 
     *  H-Alpha Telescope for Argentina
     *              
     * 
     */
    @XmlEnumValue("CUCS__HASTA")
    CUCS_HASTA("CUCS__HASTA"),

    /**
     * 
     *  Magnetograph + Intensity Synoptic  (Global Oscillation Network)
     *              
     * 
     */
    @XmlEnumValue("LEAR__GONG_SYN")
    LEAR_GONG_SYN("LEAR__GONG_SYN"),

    /**
     * 
     * Magnetograph + Intensity Synoptic  (Global Oscillation Network)
     *              
     * 
     */
    @XmlEnumValue("UDPR__GONG_SYN")
    UDPR_GONG_SYN("UDPR__GONG_SYN"),

    /**
     * 
     *  Magnetograph + Intensity Synoptic  (Global Oscillation Network)
     *              
     * 
     */
    @XmlEnumValue("TEID__GONG_SYN")
    TEID_GONG_SYN("TEID__GONG_SYN"),

    /**
     * 
     *    Magnetograph + Intensity Synoptic  (Global Oscillation Network)
     *    
     * 
     */
    @XmlEnumValue("CTIO__GONG_SYN")
    CTIO_GONG_SYN("CTIO__GONG_SYN"),

    /**
     * 
     * Magnetograph + Intensity Synoptic  (Global Oscillation Network)
     *    
     * 
     */
    @XmlEnumValue("BBSO__GONG_SYN")
    BBSO_GONG_SYN("BBSO__GONG_SYN"),

    /**
     * 
     *   Magnetograph + Intensity Synoptic  (Global Oscillation Network)
     *    
     * 
     */
    @XmlEnumValue("MLSO__GONG_SYN")
    MLSO_GONG_SYN("MLSO__GONG_SYN");
    private final String value;

    Instrument(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Instrument fromValue(String v) {
        for (Instrument c: Instrument.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
