package eu.heliovo.clientapi.query.asyncquery.impl;

import java.net.URL;
import java.util.Arrays;

import javax.xml.namespace.QName;

import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService;
import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService_Service;
import eu.helio_vo.xml.longqueryservice.v0.ResultInfo;
import eu.helio_vo.xml.longqueryservice.v0.Status;

public class DpasDemo {
    public static void main(String[] args) throws Exception {
        URL url = new URL("http://helio.mssl.ucl.ac.uk/helio-dpas/HelioLongQueryService?wsdl");
//        URL url = new URL("http://localhost:8080/helio-dpas/HelioLongQueryService?wsdl");
//         URL url = new URL("http://msslxw.mssl.ucl.ac.uk:8080/helio-ics-r3/HelioLongQueryService?wsdl");

        QName serviceName = new QName("http://helio-vo.eu/xml/LongQueryService/v0.9", "LongHelioQueryService");
        LongHelioQueryService_Service queryService = new LongHelioQueryService_Service(url, serviceName);

        LongHelioQueryService port = queryService.getLongHelioQueryServicePort();
        
        String id = port.longQuery(Arrays.asList("2010-04-01T00:00:00"),Arrays.asList("2010-06-03T00:00:00"),Arrays.asList("SOHO__CDS"),"","", 10, 0);
        Thread.sleep(1000);
        //
        Status status=port.getStatus(id);
        System.out.println(" Status "+status.getStatus()+" Status Desc "+status.getDescription()+" Status result "+status.getStatus().toString());
        String statusValue=status.getStatus().toString();
        //
        while(statusValue.equals("PENDING") ){
            Status status1=port.getStatus(id);
            statusValue=status1.getStatus().toString();
            System.out.println(" Status "+status1.getStatus()+" Status Desc "+status1.getDescription()+" Status result "+status1.getStatus().toString());
        }
        ResultInfo resultInfo=port.getResult(id);
        System.out.println(" status "+resultInfo.getStatus()+" url "+resultInfo.getResultURI());
        
        System.out.println(id);
      
    }
}
