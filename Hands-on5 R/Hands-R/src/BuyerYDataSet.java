import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BuyerYDataSet extends Agent {
    private String targetBookTitle;
    private AID[] sellerAgents;

    protected void setup() {
        System.out.println("Hello! Agent " + getAID().getName() + " is ready.");

        // Get the title of the book to buy from arguments
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            targetBookTitle = (String) args[0];
            System.out.println("Target book to buy: " + targetBookTitle);

            // Add a behavior to search for seller agents periodically
            addBehaviour(new TickerBehaviour(this, 10000) {
                protected void onTick() {
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("book-selling");
                    template.addServices(sd);

                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        sellerAgents = new AID[result.length];
                        for (int i = 0; i < result.length; ++i) {
                            sellerAgents[i] = result[i].getName();
                        }
                    } catch (FIPAException fe) {
                        fe.printStackTrace();
                    }

                    // Perform the purchase process
                    if (sellerAgents.length > 0) {
                        myAgent.addBehaviour(new PurchaseBehavior());
                    }
                }
            });
        } else {
            System.out.println("No target book title specified.");
            doDelete();
        }
    }

    protected void takeDown() {
        System.out.println("Buyer-agent " + getAID().getName() + " terminating.");
    }

    private class PurchaseBehavior extends Behaviour {
        private AID bestSeller;
        private int bestPrice;
        private int repliesCnt = 0;
        private MessageTemplate mt;
        private int step = 0;

        public void action() {
            switch (step) {
                case 0:
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < sellerAgents.length; ++i) {
                        cfp.addReceiver(sellerAgents[i]);
                    }
                    cfp.setContent(targetBookTitle);
                    cfp.setConversationId("book-trade");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis());
                    myAgent.send(cfp);

                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    step = 1;
                    break;

                case 1:
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            int price = Integer.parseInt(reply.getContent());
                            if (bestSeller == null || price < bestPrice) {
                                bestPrice = price;
                                bestSeller = reply.getSender();
                            }
                        }
                        repliesCnt++;
                        if (repliesCnt >= sellerAgents.length) {
                            step = 2;
                        }
                    } else {
                        block();
                    }
                    break;

                case 2:
                    ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    order.addReceiver(bestSeller);
                    order.setContent(targetBookTitle);
                    order.setConversationId("book-trade");
                    order.setReplyWith("order" + System.currentTimeMillis());
                    myAgent.send(order);

                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                            MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                    step = 3;
                    break;

                case 3:
                    reply = myAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            System.out.println(targetBookTitle + " successfully purchased from agent "
                                    + reply.getSender().getName());
                            System.out.println("Price = " + bestPrice);
                            myAgent.doDelete();
                        } else {
                            System.out.println("Attempt failed: requested book already sold.");
                        }
                        step = 4;
                    } else {
                        block();
                    }
                    break;
            }
        }

        public boolean done() {
            return (step == 2 && bestSeller == null) || step == 4;
        }
    }
}
