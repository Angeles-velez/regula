import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DataSetPolynomialAgent extends Agent {
    private AID classifierAgent;
    private String analysisTechnique;
    private String optimizationTechnique;
    private double[] dataX = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private double[] dataY = {1, 4, 9, 16, 25, 36, 49, 64, 81, 100}; // Quadratic relationship

    protected void setup() {
        System.out.println("Polynomial DataSet Agent " + getLocalName() + " started.");

        SequentialBehaviour sequence = new SequentialBehaviour();

        System.out.println("Searching for Classification Agent...");
        sequence.addSubBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                if (classifierAgent == null) {
                    classifierAgent = findAgent("classification-service");
                    if (classifierAgent != null) {
                        System.out.println("Classification Agent found: " + classifierAgent.getName());
                        sendDataSet(classifierAgent, dataX, dataY);
                    } else {
                        System.out.println("Classification Agent not found.");
                    }
                }
            }
        });

        sequence.addSubBehaviour(new ReceiveClassificationResponse());

        sequence.addSubBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                System.out.println("Second Tick");
                if (analysisTechnique != null && optimizationTechnique != null) {
                    AID analysisAgent = findAgent(analysisTechnique);
                    if (analysisAgent != null) {
                        System.out.println("Analysis Agent found: " + analysisAgent.getName());
                        sendDataToAgent(analysisAgent, optimizationTechnique, dataX, dataY);
                        analysisTechnique = null;
                        optimizationTechnique = null;
                        System.out.println("Sending Data to Analysis Agent");
                    } else {
                        System.out.println("Analysis Agent for " + analysisTechnique + " not found.");
                    }

                    AID optimizationAgent = findAgent(optimizationTechnique);
                    if (optimizationAgent != null) {
                        System.out.println("Optimization Agent found: " + optimizationAgent.getName());
                    } else {
                        System.out.println("Optimization Agent for " + optimizationTechnique + " not found.");
                    }
                }
            }
        });

        addBehaviour(sequence);
    }

    protected void takeDown() {
        System.out.println("Polynomial DataSet Agent " + getAID().getName() + " terminating.");
    }

    private AID findAgent(String serviceType) {
        AID agent = null;
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceType);
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                agent = result[0].getName();
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        return agent;
    }

    private void sendDataSet(AID receiver, double[] x, double[] y) {
        System.out.println("Sending DataSet...");
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(receiver);
        msg.setConversationId("dataset-classification");
        msg.setReplyWith("request" + System.currentTimeMillis());

        StringBuilder sb = new StringBuilder();
        sb.append("x:");
        for (double val : x) sb.append(val).append(",");
        sb.append(";y:");
        for (double val : y) sb.append(val).append(",");
        msg.setContent(sb.toString());

        send(msg);
    }

    private void sendDataToAgent(AID agentAID, String optimizationTechnique, double[] x, double[] y) {
        System.out.println("Sending Data to Agent");
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(agentAID);
        msg.setConversationId("analysis-optimization");
        System.out.println("Hello from Polynomial DataSet Agent");

        StringBuilder sb = new StringBuilder();
        sb.append("x:");
        for (double val : x) sb.append(val).append(",");
        sb.append(";y:");
        for (double val : y) sb.append(val).append(",");
        sb.append(";optim:").append(optimizationTechnique);
        msg.setContent(sb.toString());

        send(msg);
    }

    private class ReceiveClassificationResponse extends Behaviour {

        private MessageTemplate template;
        private boolean done = false;

        public void action() {
            System.out.println("Receiving Classification Response");
            template = MessageTemplate.MatchConversationId("dataset-classification");
            ACLMessage msg = myAgent.receive(template);
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    String response = msg.getContent();

                    System.out.println("Classification Response received: " + response);
                    String[] parts = response.split(",");
                    analysisTechnique = parts[0];
                    optimizationTechnique = parts[1];
                }
                done = true;
            } else {
                block();
            }
        }

        public boolean done() {
            return done;
        }
    }
}
