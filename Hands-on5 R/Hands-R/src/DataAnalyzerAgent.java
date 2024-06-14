import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.AID;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataAnalyzerAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("Data Analyzer Agent " + getLocalName() + " started.");

        // Registering data analysis service in the Yellow Pages (DF)
        DFAgentDescription agentDesc = new DFAgentDescription();
        agentDesc.setName(getAID());
        ServiceDescription serviceDesc = new ServiceDescription();
        serviceDesc.setType("data-analysis-service");
        serviceDesc.setName("JADE-data-analysis");
        agentDesc.addServices(serviceDesc);
        try {
            DFService.register(this, agentDesc);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // Adding behaviors
        addBehaviour(new ReceiveModelVariables());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println("Data Analyzer Agent " + getAID().getName() + " terminated.");
    }

    private class ReceiveModelVariables extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(msgTemplate);
            if (msg != null) {
                String variables = msg.getContent();
                System.out.println("Received model variables:");
                String[] parts = variables.split(";");
                String[] xValues = parts[0].replace("x:", "").split(",");
                String[] yValues = parts[1].replace("y:", "").split(",");

                System.out.println("X values:");
                for (String xValue : xValues) {
                    System.out.print(xValue);
                }

                System.out.println("");
                System.out.println("Y values:");
                for (String yValue : yValues) {
                    System.out.print(yValue);
                }
                System.out.println("");

                List<Double> listX = Arrays.stream(xValues).map(Double::parseDouble).collect(Collectors.toList());
                List<Double> listY = Arrays.stream(yValues).map(Double::parseDouble).collect(Collectors.toList());

                String analysisMethod = selectAnalysisMethod(listX, listY);
                String optimizationMethod = selectOptimizationMethod(listX, listY);

                // Send data to the designated agent
                sendDataToAgent(analysisMethod, optimizationMethod, listX, listY);

                // Send response to the requesting agent
                ACLMessage response = msg.createReply();
                response.setPerformative(ACLMessage.INFORM);
                response.setContent(analysisMethod + "," + optimizationMethod);
                myAgent.send(response);
            } else {
                block();
            }
        }

        private String selectAnalysisMethod(List<Double> x, List<Double> y) {
            System.out.println("Selecting predictive analysis technique:");
            double correlation = calculateCorrelationCoefficient(x, y);

            if (x.size() == 1) {  // Only one independent variable
                System.out.println("Using Linear Regression");
                return "linear-regression";
            } else if (x.size() > 1 && correlation > 0.8) {
                System.out.println("Using Multiple Regression");
                return "multi-regression";
            } else {
                System.out.println("Using Polynomial Regression");
                return "polynomial-regression";
            }
        }

        private double calculateCorrelationCoefficient(List<Double> x, List<Double> y) {
            int n = x.size();
            double sumX = x.stream().mapToDouble(Double::doubleValue).sum();
            double sumY = y.stream().mapToDouble(Double::doubleValue).sum();
            double sumXY = 0;
            double sumX2 = 0;
            double sumY2 = 0;
            for (int i = 0; i < n; i++) {
                sumXY += x.get(i) * y.get(i);
                sumX2 += Math.pow(x.get(i), 2);
                sumY2 += Math.pow(y.get(i), 2);
            }
            return (n * sumXY - sumX * sumY) / Math.sqrt((n * sumX2 - Math.pow(sumX, 2)) * (n * sumY2 - Math.pow(sumY, 2)));
        }

        private String selectOptimizationMethod(List<Double> x, List<Double> y) {
            System.out.println("Selecting optimization technique");
            int numVariables = x.size();

            if (numVariables < 50) {
                System.out.println("Using Genetic Algorithm");
                return "genetic-algorithm";
            } else {
                System.out.println("Using Particle Swarm Optimization");
                return "particle-swarm-optimization";
            }
        }

        private void sendDataToAgent(String analysisMethod, String optimizationMethod, List<Double> x, List<Double> y) {
            AID analysisAgentAID = findAgent(analysisMethod);
            System.out.println("Finding the corresponding analysis agent: ");
            if (analysisAgentAID == null) {
                System.out.println("Agent for " + analysisMethod + " not found.");
                return;
            }

            AID optimizationAgentAID = findAgent(optimizationMethod);
            if (optimizationAgentAID == null) {
                System.out.println("Agent for " + optimizationMethod + " not found.");
                return;
            }

            ACLMessage analysisMessage = new ACLMessage(ACLMessage.REQUEST);
            analysisMessage.addReceiver(analysisAgentAID);
            analysisMessage.setConversationId("analysis");

            ACLMessage optimizationMessage = new ACLMessage(ACLMessage.REQUEST);
            optimizationMessage.addReceiver(optimizationAgentAID);
            optimizationMessage.setConversationId("optimization");

            // Convert data to string format
            StringBuilder sb = new StringBuilder();
            sb.append("x:");
            for (Double val : x) sb.append(val).append(",");
            sb.append(";y:");
            for (Double val : y) sb.append(val).append(",");

            analysisMessage.setContent(sb.toString());
            optimizationMessage.setContent(sb.toString() + ";optim:" + optimizationMethod);

            System.out.println("Sending data to analysis agent");
            send(analysisMessage);

            System.out.println("Sending data to optimization agent");
            send(optimizationMessage);
        }

        private AID findAgent(String serviceType) {
            AID agentAID = null;
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription serviceDesc = new ServiceDescription();
            serviceDesc.setType(serviceType);
            template.addServices(serviceDesc);
            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                if (result.length > 0) {
                    agentAID = result[0].getName();
                }
            } catch (FIPAException e) {
                e.printStackTrace();
            }
            return agentAID;
        }
    }
}
