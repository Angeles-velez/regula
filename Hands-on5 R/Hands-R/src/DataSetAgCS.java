import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.Arrays;

public class DataSetAgent extends Agent {
    private AID classifierAgent;
    private String analysisMethod;
    private String optimizationMethod;
    private double[] dataX = {1.0, 2.0, 3.0, 4.0, 5.0};
    private double[] dataY = {2.0, 2.8, 3.6, 4.5, 5.1};

    protected void setup() {
        System.out.println("DataSetAgent " + getLocalName() + " started.");

        SequentialBehaviour sequence = new SequentialBehaviour();

        sequence.addSubBehaviour(new SearchClassifierBehaviour());

        sequence.addSubBehaviour(new ReceiveClassificationResponse());

        sequence.addSubBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                if (analysisMethod != null && optimizationMethod != null) {
                    AID analysisAgent = findAgent(analysisMethod);
                    if (analysisAgent != null) {
                        System.out.println("Analysis Agent found: " + analysisAgent.getName());
                        sendToAgent(analysisAgent, optimizationMethod, dataX, dataY);
                        analysisMethod = null;
                        optimizationMethod = null;
                        System.out.println("Sending Data to Analysis Agent");
                    } else {
                        System.out.println("Analysis Agent for " + analysisMethod + " not found.");
                    }
                }
            }
        });

        addBehaviour(sequence);
    }

    protected void takeDown() {
        System.out.println("DataSetAgent " + getAID().getName() + " terminating.");
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
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return agent;
    }

    private void sendDataSet(AID receiver, double[] x, double[] y) {
        System.out.println("Sending Data Set...");
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(receiver);
        msg.setConversationId("dataset-classification");
        msg.setReplyWith("request" + System.currentTimeMillis());

        StringBuilder content = new StringBuilder();
        content.append("x:").append(Arrays.toString(x)).append(";y:").append(Arrays.toString(y));
        msg.setContent(content.toString());

        send(msg);
    }

    private void sendToAgent(AID agentAID, String optimizationMethod, double[] x, double[] y) {
        System.out.println("Sending Data to Agent");
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(agentAID);
        msg.setConversationId("analysis-optimization");

        StringBuilder content = new StringBuilder();
        content.append("x:").append(Arrays.toString(x)).append(";y:").append(Arrays.toString(y));
        content.append(";optim:").append(optimizationMethod);
        msg.setContent(content.toString());

        send(msg);
    }

    private class SearchClassifierBehaviour extends OneShotBehaviour {
        public void action() {
            System.out.println("Searching for classifier agent...");
            classifierAgent = findAgent("classification-service");
            if (classifierAgent != null) {
                System.out.println("Classifier Agent found: " + classifierAgent.getName());
                sendDataSet(classifierAgent, dataX, dataY);
            } else {
                System.out.println("Classifier Agent not found.");
            }
        }
    }

    private class ReceiveClassificationResponse extends Behaviour {
        private boolean done = false;

        public void action() {
            MessageTemplate template = MessageTemplate.MatchConversationId("dataset-classification");
            ACLMessage msg = myAgent.receive(template);
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    String[] parts = msg.getContent().split(",");
                    analysisMethod = parts[0];
                    optimizationMethod = parts[1];
                    System.out.println("Classification Response received: " + analysisMethod + ", " + optimizationMethod);
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
