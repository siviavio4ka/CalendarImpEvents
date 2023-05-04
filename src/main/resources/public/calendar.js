const monthNames = [
    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
];

const calendar = document.querySelector('.calendar');

for (let monthIndex = 0; monthIndex < 12; monthIndex++) {
    const monthElem = document.createElement('div');
    monthElem.classList.add('month');

    const monthHeader = document.createElement('h3');
    monthHeader.textContent = monthNames[monthIndex];
    monthElem.appendChild(monthHeader);

    const datesContainer = document.createElement('div');
    datesContainer.classList.add('dates');
    monthElem.appendChild(datesContainer);

    const date = new Date(new Date().getFullYear(), monthIndex + 1, 0);
    const daysInMonth = date.getDate();

    for (let day = 1; day <= daysInMonth; day++) {
        const dateElem = document.createElement('a');
        dateElem.classList.add('date');
        dateElem.textContent = day;

        const year = new Date().getFullYear().toString();
        // const year = new Date().getFullYear().toString().slice(-2);
        const month = (monthIndex + 1).toString().padStart(2, '0');
        const dayFormatted = day.toString().padStart(2, '0');
        const href = `/events/${year}-${month}-${dayFormatted}`;
        dateElem.href = href;

        datesContainer.appendChild(dateElem);
    }

    calendar.appendChild(monthElem);
}


//Прошлый код
// const monthNames = [
//     "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
//     "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
// ];
//
// const calendar = document.querySelector('.calendar');
//
// for (let monthIndex = 0; monthIndex < 12; monthIndex++) {
//     const monthElem = document.createElement('div');
//     monthElem.classList.add('month');
//
//     const monthHeader = document.createElement('h3');
//     monthHeader.textContent = monthNames[monthIndex];
//     monthElem.appendChild(monthHeader);
//
//     const datesContainer = document.createElement('div');
//     datesContainer.classList.add('dates');
//     monthElem.appendChild(datesContainer);
//
//     const date = new Date(new Date().getFullYear(), monthIndex + 1, 0);
//     const daysInMonth = date.getDate();
//
//     for (let day = 1; day <= daysInMonth; day++) {
//         const dateElem = document.createElement('a');
//         dateElem.classList.add('date');
//         dateElem.textContent = day;
//         dateElem.href = `'/events/' + ${daysInMonth} + ${monthIndex + 1} + ${day}`;
//         // dateElem.href = `home.html?month=${monthIndex + 1}&day=${day}`;
//         datesContainer.appendChild(dateElem);
//     }
//
//     calendar.appendChild(monthElem);
// }