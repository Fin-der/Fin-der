import { app } from '../server.js' // Link to your server file
import supertest from 'supertest'

const request = supertest(app)

// afterEach(() => {
//     app.db
// });

it('gets the test endpoint', async done => {
    const response = await request.get('/test')
  
    expect(response.status).toBe(200)
    expect(response.body.message).toBe('pass!')
    done()
})

/* 
it ('tests onGetAllUsers', async done => {
    const response = await request.get('/users')
  
    expect(response.status).toBe(200)
    done()
}) */