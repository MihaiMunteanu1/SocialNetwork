package org.example.llab67.service;

import org.example.llab67.domain.Message;
import org.example.llab67.domain.Utilizator;
import org.example.llab67.domain.Prietenie;
import org.example.llab67.domain.FriendshipDTO;
import org.example.llab67.exceptions.ServiceException;
import org.example.llab67.repository.Repository;
import org.example.llab67.repository.paging.FriendRequestPagingRepository;
import org.example.llab67.repository.paging.PagingRepository;
import org.example.llab67.utils.events.ChangeEventType;
import org.example.llab67.utils.events.UtilizatorEntityChangeEvent;
import org.example.llab67.utils.observer.Observer;
import org.example.llab67.utils.observer.Observable;
import org.example.llab67.utils.paging.Page;
import org.example.llab67.utils.paging.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service implements Observable<UtilizatorEntityChangeEvent>{
    private final Repository<Long, Utilizator> repoUtilizator;
    private final FriendRequestPagingRepository<Long, Prietenie> repoPrietenie;
    private final List<Observer<UtilizatorEntityChangeEvent>> observers=new ArrayList<>();

    private final Repository<Long, Message> repoMessages;


    public Service(Repository<Long, Utilizator> repoUtilizator,
                   FriendRequestPagingRepository<Long, Prietenie> repoPrietenie,Repository<Long, Message> repoMessages) {
        this.repoUtilizator = repoUtilizator;
        this.repoPrietenie = repoPrietenie;
        this.repoMessages = repoMessages;
    }

    public Page<FriendshipDTO> findAllUserFriends(Pageable pageable, Utilizator user) {
        Page<Prietenie> pgF = repoPrietenie.findAllOnPage(pageable, user.getId());
        List<FriendshipDTO> friendshipDTOs = StreamSupport.stream(pgF.getElementsOnPage().spliterator(), false)
                .filter(prietenie -> prietenie.contineId(user.getId()) && !prietenie.isPending())
                .map(prietenie -> {
                    Long friendId = prietenie.getId1().equals(user.getId()) ? prietenie.getId2() : prietenie.getId1();
                    Utilizator friend = repoUtilizator.findOne(friendId).orElse(null);
                    if (friend != null) {
                        return new FriendshipDTO(prietenie.getId(), friend.getFirstName(), friend.getLastName(), friend.getEmail(), prietenie.getFriendsFrom());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new Page<>(friendshipDTOs, pgF.getTotalElementCount());    }



    @Override
    public void addObserver(Observer<UtilizatorEntityChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<UtilizatorEntityChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(UtilizatorEntityChangeEvent t) {

        observers.stream().forEach(x->x.update(t));
    }

    public boolean checkIfEmailExists(String email) {
        return StreamSupport.stream(repoUtilizator.findAll().spliterator(), false)
                .anyMatch(utilizator -> Objects.equals(utilizator.getEmail(), email));
    }

    public Utilizator findOneByMailAndPassword(String mail, String password) {
        return StreamSupport.stream(repoUtilizator.findAll().spliterator(), false)
                .filter(utilizator -> Objects.equals(utilizator.getEmail(), mail) && Objects.equals(utilizator.getPassword(), password))
                .findFirst().orElse(null);
    }

    public List<Utilizator> findOneByName(String name, String Lastname) {
        return StreamSupport.stream(repoUtilizator.findAll().spliterator(), false)
                .filter(utilizator -> Objects.equals(utilizator.getFirstName(), name) &&Objects.equals(utilizator.getFirstName(), Lastname))
                .collect(Collectors.toList());
    }

    public List<Utilizator> findByFirstName(String firstName) {
        return StreamSupport.stream(repoUtilizator.findAll().spliterator(), false)
                .filter(utilizator -> utilizator.getFirstName().contains(firstName))
                .collect(Collectors.toList());
    }

    public List<Utilizator> findByLastName(String lastName) {
        return StreamSupport.stream(repoUtilizator.findAll().spliterator(), false)
                .filter(utilizator -> utilizator.getLastName().contains(lastName))
                .collect(Collectors.toList());
    }

    public LocalDateTime getDataPrieteniei(Long id1, Long id2) {
        StringBuilder exceptii = new StringBuilder();

        if (repoUtilizator.findOne(id1).isEmpty()) {
            exceptii.append("Utilizatorul: ").append(id1).append(" nu exista. ");
        }

        if (repoUtilizator.findOne(id2).isEmpty()) {
            exceptii.append("Utilizatorul: ").append(id2).append(" nu exista. ");
        }

        if (!exceptii.isEmpty()) {
            throw new ServiceException(exceptii.toString());
        }

        return StreamSupport.stream(repoPrietenie.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.contineId(id1) && prietenie.contineId(id2))
                .map(Prietenie::getFriendsFrom)
                .findFirst()
                .orElse(null);
    }

    public List<FriendshipDTO> getFriendRequests(Long userId) {
        return StreamSupport.stream(repoPrietenie.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.getId2().equals(userId) && prietenie.isPending())
                .map(prietenie -> {
                    Utilizator friend = repoUtilizator.findOne(prietenie.getId1()).orElse(null);
                    if (friend != null) {
                        return new FriendshipDTO(prietenie.getId(), friend.getFirstName(), friend.getLastName(), friend.getEmail(), prietenie.getFriendsFrom());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public List<Utilizator> getPrieteniiUnuiUtilizator(Long userId) {
        return StreamSupport.stream(repoPrietenie.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.contineId(userId) && !prietenie.isPending())
                .map(prietenie -> {
                    Long friendId;
                    if (prietenie.getId1().equals(userId)) {
                        friendId = prietenie.getId2();
                    } else {
                        friendId = prietenie.getId1();
                    }
                    return repoUtilizator.findOne(friendId).orElse(null);
                })
                .collect(Collectors.toList());
    }

    public Utilizator findOneUserByNameAndEmail(String firstName, String lastName, String email){
        return StreamSupport.stream(repoUtilizator.findAll().spliterator(), false)
                .filter(utilizator -> Objects.equals(utilizator.getFirstName(), firstName) && Objects.equals(utilizator.getLastName(), lastName) && Objects.equals(utilizator.getEmail(), email))
                .findFirst().orElse(null);
    }

    public List<Prietenie> getPendingFriendRequests(Long userId) {
        return StreamSupport.stream(repoPrietenie.findAll().spliterator(), false)
                .filter(prietenie -> Objects.equals(prietenie.getId2(),userId) && prietenie.isPending())
                .collect(Collectors.toList());
    }

    public Iterable<Utilizator> getUtilizatori() {
        return repoUtilizator.findAll();
    }

    public Optional<Utilizator> findUtilziator(Long id) {
        return repoUtilizator.findOne(id);
    }

    public Long getNewUserID() {
        return StreamSupport.stream(repoUtilizator.findAll().spliterator(), false)
                .mapToLong(Utilizator::getId)
                .max()
                .orElse(0L) + 1;
    }

    public Utilizator addUtilizator(Utilizator u) {
        List<Utilizator> utilizatoriCuAcelasiEmail = StreamSupport.stream(repoUtilizator.findAll().spliterator(), false)
                .filter(utilizator -> utilizator.getEmail().equals(u.getEmail()))
                .toList();

        if (!utilizatoriCuAcelasiEmail.isEmpty()) {
            throw new ServiceException("Exista deja un utilizator cu acest email");
        }
        else{
            u.setId(getNewUserID());
            if(repoUtilizator.save(u).isEmpty()){
                UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.ADD, u);
                notifyObservers(event);
                return null;
            }
            return u;
        }

    }

    public Iterable<Prietenie> getPrietenii() {
        return repoPrietenie.findAll();
    }

    public Utilizator removeUtilizator(Long id) {
        List<Long> prietenieIdsToDelete = StreamSupport.stream(repoPrietenie.findAll().spliterator(), false)
                .filter(prietenie -> Objects.equals(prietenie.getId1(), id) || Objects.equals(prietenie.getId2(), id))
                .map(Prietenie::getId)
                .toList();

        prietenieIdsToDelete.forEach(repoPrietenie::delete);

        Optional<Utilizator> u= repoUtilizator.delete(id);
        if(u.isPresent()){
            notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.DELETE, u.get()));
            return u.get();
        }
        return null;

    }

    public Long getNewPrietenieID() {
        return StreamSupport.stream(repoPrietenie.findAll().spliterator(), false)
                .mapToLong(Prietenie::getId)
                .max()
                .orElse(0L) + 1;
    }


    public void addPrietenie(Prietenie prietenie) {
        if (getPrietenii() != null) {
            for (Prietenie p : getPrietenii())
                if (p.getId1().equals(prietenie.getId1()) && p.getId2().equals(prietenie.getId2())
                        || p.getId1().equals(prietenie.getId2()) && p.getId2().equals(prietenie.getId1()))
                    throw new ServiceException("Prietenia exista");

            if (repoUtilizator.findOne(prietenie.getId1()).isEmpty() || repoUtilizator.findOne(prietenie.getId2()).isEmpty())
                throw new ServiceException("Utilizatorul nu exista");

            if(repoUtilizator.findOne(prietenie.getId1()).get().equals(repoUtilizator.findOne(prietenie.getId2()).get()))
                throw new ServiceException("Nu te poti adauga singur ca prieten");

            if (prietenie.getId1().equals(prietenie.getId2()))
                throw new ServiceException("ID-urile nu pot fi identice");
        }
        prietenie.setId(getNewPrietenieID());
        repoPrietenie.save(prietenie);

    }


    public void removePrietenie(Long id1, Long id2) {
        Long idPrietenie = StreamSupport.stream(repoPrietenie.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.contineId(id1) && prietenie.contineId(id2))
                .map(Prietenie::getId)
                .findFirst()
                .orElse(null);

        repoPrietenie.delete(idPrietenie);

        List<Message> messagesToDelete = getMessages(id1, id2);
        messagesToDelete.forEach(message -> repoMessages.delete(message.getId()));
    }

    public void removePrietenieP(Long idPrietenie) {
        Prietenie prietenie = repoPrietenie.findOne(idPrietenie).orElseThrow(() -> new ServiceException("Prietenia nu există."));
        repoPrietenie.delete(idPrietenie);

        List<Message> messagesToDelete = getMessages(prietenie.getId1(), prietenie.getId2());
        for (Message message : messagesToDelete) {
            repoMessages.delete(message.getId());
        }
    }

    public Prietenie findOnePrietenie(Long id1 ,Long id2) {
        return StreamSupport.stream(repoPrietenie.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.contineId(id1) && prietenie.contineId(id2))
                .findFirst()
                .orElse(null);
    }

    public Utilizator updateUtilizator(Utilizator u) {
        Optional<Utilizator> oldUser = repoUtilizator.findOne(u.getId());
        if (oldUser.isPresent()) {
            Optional<Utilizator> newUser = repoUtilizator.update(u);
            if (newUser.isEmpty()) {
                notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.UPDATE, u, oldUser.get()));
            }
            return newUser.orElse(null);
        }
        return null;
    }

    public void acceptFriendRequest(Long id, Long id1) {
        List<Prietenie> prietenii = StreamSupport.stream(repoPrietenie.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.contineId2(id) && prietenie.contineId2(id1) && prietenie.isPending())
                .toList();



        if (prietenii.size() != 1) {
            throw new ServiceException("Nu exista o cerere de prietenie intre cei doi utilizatori");
        }

        Prietenie prietenie = prietenii.getFirst();
        prietenie.setPending(false);
        repoPrietenie.update(prietenie);


    }

    public void declineFriendRequest(Long id, Long id1) {
        List<Prietenie> prietenii = StreamSupport.stream(repoPrietenie.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.contineId(id) && prietenie.contineId(id1) && prietenie.isPending())
                .toList();

        if (prietenii.size() != 1) {
            throw new ServiceException("Nu exista o cerere de prietenie intre cei doi utilizatori");
        }

        Prietenie prietenie = prietenii.getFirst();
        repoPrietenie.delete(prietenie.getId());
    }

    public boolean addMessage(Long id_from, Long id_to, String message) {
        try {
            Optional<Utilizator> from = findUtilziator(id_from);
            Optional<Utilizator> to = findUtilziator(id_to);

            if (from.isPresent() && to.isPresent()) {
                if (message == null || message.trim().isEmpty()) {
                    throw new ServiceException("Mesajul nu poate fi gol");
                }

                Message msg = new Message(from.get(), Collections.singletonList(to.get()), message);
                repoMessages.save(msg);

                List<Message> messagesBetweenUsers = getMessages(id_to, id_from);
                if (messagesBetweenUsers.size() > 1) {
                    Message oldReplyMessage = messagesBetweenUsers.get(messagesBetweenUsers.size() - 2);
                    Message newReplyMessage = messagesBetweenUsers.get(messagesBetweenUsers.size() - 1);
                    oldReplyMessage.setReply(newReplyMessage);
                    repoMessages.update(oldReplyMessage);
                }
                else if(messagesBetweenUsers.size() == 1) {
                    Message newReplyMessage = messagesBetweenUsers.getFirst();
                    newReplyMessage.setReply(newReplyMessage);
                    repoMessages.update(newReplyMessage);
                }

                return true;
            } else {
                throw new ServiceException("Unul sau ambii utilizatori nu au fost găsiți");
            }
        } catch (ServiceException se) {
            System.out.println("Eroare utilizator: " + se.getMessage());
        } catch (Exception ex) {
            System.out.println("Eroare creare mesaj: " + ex.getMessage());
        }
        return false;
    }

    public ArrayList<Message> getMessages(Long id1, Long id2) {
        Optional<Utilizator> user1 = findUtilziator(id1);
        Optional<Utilizator> user2 = findUtilziator(id2);

        Collection<Message> messages = (Collection<Message>) repoMessages.findAll();

        return messages.stream()
                .filter(msg -> ((msg.getFrom().getId().equals(id1)) && msg.getTo().getFirst().getId().equals(id2)) ||
                        (msg.getFrom().getId().equals(id2) && msg.getTo().getFirst().getId().equals(id1)))
                .sorted(Comparator.comparing(Message::getData))
                .collect(Collectors.toCollection(ArrayList::new));


    }
}

