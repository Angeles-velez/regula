import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.Random;

public class DataSetAgentPSO extends Agent {
    private AID classificationAgent;
    private String analysisTechnique;
    private String optimizationTechnique;
    private double[] dataX = new double[100];
    private double[] dataY = new double[100];

    protected void setup() {
        System.out.println("Agent with Dataset " + getLocalName() + " started.");

        // Generate random values for dataX and dataY
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            dataX[i] = rand.nextDouble() * 100; // Random values between 0 and 100
            dataY[i] = rand.nextDouble() * 100; // Random values between 0 and 100
        }

        SequentialBehaviour sequence = new SequentialBehaviour();

        // Search for classification agent
        sequence.addSubBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                if (classificationAgent == null) {
                    classificationAgent = findAgent("classification-service");
                    if (classificationAgent != null) {
                        System.out.println("Classification Agent found: " + classificationAgent.getName());
                        sendDataSet(classificationAgent, dataX, dataY);
                    } else {
                        System.out.println("Classification Agent not found.");
                    }
                }
            }
        });

        // Receive classification response
        sequence.addSubBehaviour(new ReceiveClassification());

        // Tick for optimization
        sequence.addSubBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                if (analysisTechnique != null && optimizationTechnique != null) {
                    AID analysisAgent = findAgent(analysisTechnique);
                    if (analysisAgent != null) {
                        System.out.println("Analysis Agent found: " + analysisAgent.getName());
                        sendToAgent(analysisAgent, optimizationTechnique, dataX, dataY);
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
        System.out.println("DataSetAgentPSO " + getAID().getName() + " terminating.");
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

    private void sendToAgent(AID agentAID, String optimizationTechnique, double[] x, double[] y) {
        System.out.println("Sending Data to Agent");
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(agentAID);
        msg.setConversationId("analysis-optimization");

        StringBuilder sb = new StringBuilder();
        sb.append("x:");
        for (double val : x) sb.append(val).append(",");
        sb.append(";y:");
        for (double val : y) sb.append(val).append(",");
        sb.append(";optim:").append(optimizationTechnique);
        msg.setContent(sb.toString());

        send(msg);
    }

    private class ReceiveClassification extends Behaviour {
        private boolean done = false;

        public void action() {
            MessageTemplate mt = MessageTemplate.MatchConversationId("dataset-classification");
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    String response = msg.getContent();
                    System.out.println("Classification response received: " + response);
                    String[] parts = response.split(",");
                    if (parts.length >= 2) {
                        analysisTechnique = parts[0];
                        optimizationTechnique = parts[1];
                    } else {
                        System.out.println("Invalid message format: " + response);
                    }
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

