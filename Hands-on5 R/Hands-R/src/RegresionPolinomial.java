import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.Arrays;

public class RegresionPolinomial extends Agent {

    protected void setup() {
        System.out.println("Agent " + getLocalName() + " started.");

        // Registrar el servicio de regresión polinomial en el Directory Facilitator
        registrarServicio("PolyRAgent", "regresion-polinomial");

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

    private class ReceiveDataSet extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String response = msg.getContent(); // Supongamos que el mensaje contiene el dataset
                String[] parts = response.split(";");

                // Extraer los datos del dataset
                String[] xValues = parts[0].replace("x:", "").split(",");
                String[] yValues = parts[1].replace("y:", "").split(",");

                // Convertir los datos a un formato adecuado para tu código de regresión polinomial
                double[] xData = Arrays.stream(xValues).mapToDouble(Double::parseDouble).toArray();
                double[] yData = Arrays.stream(yValues).mapToDouble(Double::parseDouble).toArray();

                // Crear el objeto para la regresión polinomial (grado 3 por defecto)
                PolinomialRegression polyReg = new PolinomialRegression(xData, yData, 3); // Grado del polinomio

                // Realizar cálculos y operaciones de la regresión polinomial
                polyReg.fit();
                polyReg.printRegEquation();
                polyReg.predict(6); // Ejemplo de predicción para x = 6

                // Otros pasos de tu lógica después de la regresión polinomial
            } else {
                block();
            }
        }
    }
}

