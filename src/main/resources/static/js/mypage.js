function showMyReservations() {
    const myPageSection = document.getElementById('mypage-section');

    fetch('/api/users/me/reservations', { credentials: 'include' })
        .then(res => {
            if (res.status === 401 || res.status === 403) {
                handle401Error();
                return null;
            }

            if (!res.ok) {
                throw new Error("reservation-api-not-ready");
            }

            return res.json();
        })
        .then(reservations => {
            if (!reservations) return;

            const reservationItems = reservations.length === 0
                ? `<div class="empty-state">예매 내역이 없습니다.</div>`
                : reservations.map(reservation => `
                    <article class="reservation-item" onclick="showMyReservationDetail(${reservation.reservationId})">
                        <img class="reservation-image" src="${reservation.concertImage || '/images/mingpark-logo.png'}" alt="${reservation.concertTitle || '공연 이미지'}">
                        <div class="reservation-info">
                            <h3>${reservation.concertTitle || '-'}</h3>
                            <p><strong>공연명:</strong> ${reservation.concertTitle || '-'}</p>
                            <p><strong>예매 번호:</strong> ${reservation.reservationNumber || reservation.reservationId || '-'}</p>
                            <p><strong>관람 일시:</strong> ${formatConcertDateTime(reservation.concertDate, reservation.concertTime)}</p>
                            <p><strong>장소:</strong> ${reservation.place || '장소 미정'}</p>
                            <p><strong>상태:</strong> ${formatReservationStatus(reservation.status)}</p>
                        </div>
                        <div class="reservation-arrow">&gt;</div>
                    </article>
                `).join('');

            myPageSection.innerHTML = `
                <h2>내 예매 내역</h2>
                <div class="reservation-list">
                    ${reservationItems}
                </div>
                <div class="actions reservation-actions">
                    <button class="btn btn-secondary" onclick="showMyPage()">마이페이지로</button>
                </div>
            `;

            document.querySelectorAll('.page-section').forEach(s => s.classList.remove('active'));
            myPageSection.classList.add('active');
        })
        .catch(() => {
            myPageSection.innerHTML = `
                <h2>내 예매 내역</h2>
                <div class="empty-state">
                    예매 내역 API가 아직 준비되지 않았습니다.<br>
                    나중에 GET /api/users/me/reservations 연결 후 다시 확인해 주세요.
                </div>
                <div class="actions reservation-actions">
                    <button class="btn btn-secondary" onclick="showMyPage()">마이페이지로</button>
                </div>
            `;
        });
}

function showMyReservationDetail(reservationId) {
    const myPageSection = document.getElementById('mypage-section');

    fetch(`/api/users/me/reservations/${reservationId}`, { credentials: 'include' })
        .then(res => {
            if (res.status === 401 || res.status === 403) {
                handle401Error();
                return null;
            }

            if (!res.ok) {
                throw new Error("reservation-detail-api-not-ready");
            }

            return res.json();
        })
        .then(reservation => {
            if (!reservation) return;

            myPageSection.innerHTML = `
                <h2>예매 내역 상세</h2>
                <section class="reservation-detail">
                    <img class="reservation-detail-image" src="${reservation.concertImage || '/images/mingpark-logo.png'}" alt="${reservation.concertTitle || '공연 이미지'}">
                    <div class="reservation-detail-info">
                        <h3>${reservation.concertTitle || '-'}</h3>
                        <p><strong>공연명:</strong> ${reservation.concertTitle || '-'}</p>
                        <p><strong>좌석 번호:</strong> ${reservation.seatNumber || '-'}</p>
                        <p><strong>결제 금액:</strong> ${formatPrice(reservation.totalPrice)}</p>
                        <p><strong>예매 상태:</strong> ${formatReservationStatus(reservation.status)} ${formatStatusTime(reservation)}</p>
                        <p><strong>취소 가능 여부:</strong> ${reservation.cancellable ? '취소 가능' : '취소 불가'}</p>
                        <p><strong>예매 번호:</strong> ${reservation.reservationNumber || reservation.reservationId || '-'}</p>
                        <p><strong>예매일:</strong> ${formatDateTime(reservation.createdAt || reservation.reservedAt)}</p>
                    </div>
                </section>

                <section class="seat-summary">
                    <h3>좌석 정보</h3>
                    <div class="info-row"><span class="info-label">예매 번호</span><span>${reservation.reservationNumber || reservation.reservationId || '-'}</span></div>
                    <div class="info-row"><span class="info-label">좌석 번호</span><span>${reservation.seatNumber || '-'}</span></div>
                    <div class="info-row"><span class="info-label">가격</span><span>${formatPrice(reservation.totalPrice)}</span></div>
                </section>

                <div class="actions reservation-actions">
                    <button class="btn btn-secondary" onclick="showMyReservations()">목록으로</button>
                    ${reservation.cancellable ? `
                        <button class="btn btn-refund" onclick="refundReservation(${reservation.reservationId})">
                            환불하기
                        </button>
                    ` : ''}
                </div>
            `;
        })
        .catch(() => {
            myPageSection.innerHTML = `
                <h2>예매 내역 상세</h2>
                <div class="empty-state">
                    예매 상세 API가 아직 준비되지 않았습니다.<br>
                    나중에 GET /api/users/me/reservations/${reservationId} 연결 후 다시 확인해 주세요.
                </div>
                <div class="actions reservation-actions">
                    <button class="btn btn-secondary" onclick="showMyReservations()">목록으로</button>
                </div>
            `;
        });
}

function refundReservation(reservationId) {
    if (!confirm('정말 환불하시겠습니까?')) return;

    fetch(`/api/users/me/reservations/${reservationId}/refund`, {
        method: 'POST',
        credentials: 'include'
    })
        .then(res => {
            if (res.status === 401 || res.status === 403) {
                handle401Error();
                return null;
            }

            return res.json();
        })
        .then(result => {
            if (!result) return;

            if (result.status === 'success') {
                alert(result.message || '환불이 완료되었습니다.');
                showMyReservations();
                return;
            }

            alert(result.message || '환불에 실패했습니다.');
        })
        .catch(() => {
            alert('환불 처리 중 오류가 발생했습니다.');
        });
}

function formatReservationStatus(status) {
    if (status === 'PENDING') return '결제 대기';
    if (status === 'RESERVED') return '예매 완료';
    if (status === 'CANCELLED') return '취소 완료';
    return status || '-';
}

function formatConcertDateTime(date, time) {
    if (!date && !time) return '-';
    if (!date) return time;
    if (!time) return date;
    return `${date} ${String(time).slice(0, 5)}`;
}

function formatDateTime(value) {
    if (!value) return '-';
    return String(value).replace('T', ' ').slice(0, 16);
}

function formatStatusTime(reservation) {
    const time = reservation.confirmedAt || reservation.cancelledAt || reservation.reservedAt;
    return time ? `(${formatDateTime(time)})` : '';
}

function formatPrice(price) {
    if (price === null || price === undefined) return '-';
    return `${Number(price).toLocaleString()}원`;
}
