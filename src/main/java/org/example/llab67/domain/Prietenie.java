    package org.example.llab67.domain;

    import java.time.LocalDateTime;
    import java.util.Objects;

    public class Prietenie extends Entity<Long> {
        private Long id1;
        private Long id2;
        LocalDateTime friendsFrom;
        private boolean pending;

        public Prietenie(Long id1, Long id2, LocalDateTime friendsFrom) {
            this.id1 = id1;
            this.id2 = id2;
            this.friendsFrom = friendsFrom;
            this.pending = true;
        }
        public boolean isPending() {
            return pending;
        }
        public void setPending(boolean pending) {
            this.pending = pending;
        }
        public LocalDateTime getFriendsFrom() {
            return friendsFrom;
        }

        public void setFriendsFrom(LocalDateTime friendsFrom) {
            this.friendsFrom = friendsFrom;
        }

        public Long getId1() {
            return id1;
        }

        public void setId1(Long id1) {
            this.id1 = id1;
        }

        public Long getId2() {
            return id2;
        }

        public void setId2(Long id2) {
            this.id2 = id2;
        }


        public boolean contineId(Long id) {
            return Objects.equals(id1, id) || Objects.equals(id2, id);
        }

        public boolean contineId2(Long id) {
            return id1.equals(id) || id2.equals(id);
        }
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Prietenie prietenie = (Prietenie) o;
            return pending == prietenie.pending && Objects.equals(id1, prietenie.id1) && Objects.equals(id2, prietenie.id2) && Objects.equals(friendsFrom, prietenie.friendsFrom);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id1, id2, friendsFrom, pending);
        }

        @Override
        public String toString() {
            return "Prietenie{" +
                    "id1=" + id1 +
                    ", id2=" + id2 +
                    ", friendsFrom=" + friendsFrom +
                    ", pending=" + pending +
                    '}';
        }
    }