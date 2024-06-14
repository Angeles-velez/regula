import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.Arrays;

public class regresionLineal extends Agent {

    protected void setup() {
        System.out.println("Agent " + getLocalName() + " started.");

        // Registrar el servicio de regresión lineal simple en el Directory Facilitator
        registrarServicio("SLRAgent", "regresion-lineal");

        // Comportamiento para recibir el dataset del DataSetAgent
        addBehaviour(new ReceiveDataSet());
    }

    protected void takeDown() {
        // Deregistrar el servicio al terminar el agente
        deregistrarServicio();
        System.out.println("Agent " + getLocalName() + " terminating.");
    }

    private void registrarServicio(String agentName, String serviceType) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceType);
        sd.setName(agentName);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            System.out.println("Service registered: " + agentName + " - " + serviceType);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private void deregistrarServicio() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public static class ReceiveDataSet extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String response = msg.getContent(); // Supongamos que el mensaje contiene el dataset
                String[] parts = response.split(";");

                // Extraer los datos del dataset
                String[] xValues = parts[0].replace("x:", "").split(",");
                String[] yValues = parts[1].replace("y:", "").split(",");

                // Convertir los datos a un formato adecuado para regresión lineal
                double[] xData = Arrays.stream(xValues).mapToDouble(Double::parseDouble).toArray();
                double[] yData = Arrays.stream(yValues).mapToDouble(Double::parseDouble).toArray();

                // Realizar operaciones de regresión lineal (ejemplo)
                // Aquí puedes implementar tu lógica específica de regresión lineal
                performLinearRegression(xData, yData);
            } else {
                block();
            }
        }

        private void performLinearRegression(double[] xData, double[] yData) {
            // Implementación de la regresión lineal
            // Aquí puedes incluir tu lógica específica para calcular la pendiente, intersección, etc.
            // Ejemplo básico:
            double meanX = Arrays.stream(xData).sum() / xData.length;
            double meanY = Arrays.stream(yData).sum() / yData.length;

            double numerator = 0.0;
            double denominator = 0.0;

            for (int i = 0; i < xData.length; i++) {
                numerator += (xData[i] - meanX) * (yData[i] - meanY);
                denominator += Math.pow((xData[i] - meanX), 2);
            }

            double B1 = numerator / denominator;
            double B0 = meanY - B1 * meanX;

            System.out.println("Resultado de regresión lineal:");
            System.out.println("Pendiente (B1): " + B1);
            System.out.println("Intersección (B0): " + B0);
            System.out.println("Ecuación de Regresión: y = " + B0 + " + " + B1 + "x");
        }
    }
}
